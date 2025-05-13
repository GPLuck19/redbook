package com.gp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.gp.common.enums.TradeStatusEnum;
import com.gp.dto.port.PayRequest;
import com.gp.dto.resp.pay.PayCodeUrlResult;
import com.gp.entity.TOrder;
import com.gp.entity.TPay;
import com.gp.exception.ServiceException;
import com.gp.mapper.TOrderMapper;
import com.gp.mapper.TPayMapper;
import com.gp.service.TbPayWxService;
import com.gp.strategy.AbstractStrategyChoose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
* @author Administrator
* @description 针对表【tb_pay_wx】的数据库操作Service实现
* @createDate 2024-09-20 14:20:01
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class TbPayWxServiceImpl implements TbPayWxService{

    private final AbstractStrategyChoose abstractStrategyChoose;


    private final TPayMapper payMapper;

    private final WxPayService wxPayService;

    private final TOrderMapper orderMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayCodeUrlResult commonPay(PayRequest requestParam) {
        PayCodeUrlResult result = abstractStrategyChoose.chooseAndExecuteResp(requestParam.buildMark(), requestParam);
        TPay insertPay = BeanUtil.toBean(requestParam, TPay.class);
        String orderSn = requestParam.getOrderSn();
        insertPay.setStatus(TradeStatusEnum.WAIT_BUYER_PAY.tradeCode());
        insertPay.setOrderSn(orderSn);
        insertPay.setTotalAmount(requestParam.getTotalAmount().multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP));
        int insert = payMapper.insert(insertPay);
        if (insert <= 0) {
            log.error("支付单创建失败，支付聚合根：{}", JSON.toJSONString(requestParam));
            throw new ServiceException("支付单创建失败!");
        }
        return result;
    }

    @Override
    @Transactional
    public void notifyPayResult(String xmlResultString) {
        //1.判断关键数据并解析
        if (ObjectUtil.isNull(xmlResultString)) {
            throw new ServiceException("微信数据支付解析出错!");
        }
        WxPayOrderNotifyResult result = null;
        try {
            result = wxPayService.parseOrderNotifyResult(xmlResultString);
        } catch (WxPayException e) {
            throw new ServiceException("微信数据返回异常!");
        }
        //判断第三方支付是否成功：return_code 和 result_code 必须为SUCCESS
        String returnCode = result.getReturnCode();
        String resultCode = result.getResultCode();
        String wxPaySuccessFlag = PayCodeUrlResult.WX_PAY_SUCCESS_FLAG;
        if (wxPaySuccessFlag.equalsIgnoreCase(returnCode) && wxPaySuccessFlag.equalsIgnoreCase(resultCode)) {
            // 2.判断消息的幂等性（第三方支付平台会有重复通知的消息）
            String orderId = result.getOutTradeNo();
            LambdaQueryWrapper<TPay> queryWrapper = Wrappers.lambdaQuery(TPay.class)
                    .eq(TPay::getOrderSn, orderId);
            TPay payDO = payMapper.selectOne(queryWrapper);
            if (Objects.isNull(payDO)) {
                log.error("支付单不存在，orderRequestId：{}", orderId);
                throw new ServiceException("支付单不存在!");
            }
            // 3.判断业务数据
            //    订单数据（已经在第二步操作完毕）
            // PS:wx文档中强调--用户所支付的金额要和本地的订单金额一致
            // 单位（分）
            Integer totalFee = result.getTotalFee();
            String toYuanStr = BaseWxPayResult.fenToYuan(totalFee);
            Float toYuan = Float.valueOf(toYuanStr);
            // 单位（元）
            BigDecimal initialPrice = payDO.getTotalAmount();
            if (!(ObjectUtils.nullSafeEquals(initialPrice,toYuan))) {
                log.error("支付前后价格不同："+"order-price {}  ,  pay-price {},  orderId{} ",payDO.getTotalAmount(), toYuanStr,payDO.getOrderSn());
                throw new ServiceException("支付前后价格不同!");
            }
            //    订单支付数据
            //        判断是否存在和支付状态：未支付
            LambdaQueryWrapper<TPay> payQueryWrapper = new LambdaQueryWrapper<>();
            payQueryWrapper.eq(TPay::getOrderSn,orderId);
            payQueryWrapper.eq(TPay::getStatus, PayCodeUrlResult.NOT_PAY);
            TPay tPayOrder = payMapper.selectOne(payQueryWrapper);
            if (ObjectUtils.isEmpty(tPayOrder)) {
                throw new ServiceException("支付订单不存在!");
            }
            //     订单支付数据
            //       pay_number--第三方支付的订单号
            //       pay_method--支付方式（wx）
            //       pay_date--用户在第三方支付平台支付的金额的时间
            //       金额
            //          订单应支付金额、订单实际支付金额
            //       pay_response--第三方支付平台通知的所有数据结果内容（xml）
            //       支付状态：已支付-1

            String tradeType = result.getTradeType();
            tPayOrder.setChannel(tradeType);
            // yyyyMMddHHmmss
            String timeEnd = result.getTimeEnd();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            /*LocalDateTime payDate = LocalDateTime.parse(timeEnd, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));*/
            try {
                tPayOrder.setGmtPayment(simpleDateFormat.parse(timeEnd));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            // 订单应支付金额
            Integer settlementTotalFee = result.getSettlementTotalFee();
            if (!(ObjectUtils.isEmpty(settlementTotalFee))) {
                String settlementStr = BaseWxPayResult.fenToYuan(settlementTotalFee);
                tPayOrder.setTotalAmount(new BigDecimal(settlementStr));
            }
            // 用户所支付金额
            Integer cashFee = result.getCashFee();
            if (!(ObjectUtils.isEmpty(cashFee))) {
                String cashStr = BaseWxPayResult.fenToYuan(cashFee);
                tPayOrder.setPayAmount(new BigDecimal(cashStr));
            }
            tPayOrder.setStatus(PayCodeUrlResult.PAIED);
            int count = payMapper.updateById(tPayOrder);
            if (count<=0) {
                throw new ServiceException("支付单更新失败!");
            }
            if (Objects.equals(tPayOrder.getStatus(), TradeStatusEnum.TRADE_SUCCESS.tradeCode())) {
                TOrder order = new TOrder();
                order.setPayTime(tPayOrder.getGmtPayment());
                order.setPayType(Integer.valueOf(tPayOrder.getChannel()));
                order.setStatus(Integer.valueOf(tPayOrder.getStatus()));
                LambdaUpdateWrapper<TOrder> updateWrapper = Wrappers.lambdaUpdate(TOrder.class)
                        .eq(TOrder::getOrderSn, tPayOrder.getOrderSn());
                int updateResult = orderMapper.update(order, updateWrapper);
                if (updateResult <= 0) {
                    throw new ServiceException("订单状态支付修改失败!");
                }
            }
        } else {
            // 支付通知结果内容失败
            throw new ServiceException("支付通知结果内容失败!");
        }
    }
}




