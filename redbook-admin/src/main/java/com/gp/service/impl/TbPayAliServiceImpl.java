/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gp.common.enums.TradeStatusEnum;
import com.gp.convert.RefundRequestConvert;
import com.gp.dto.port.PayRequest;
import com.gp.dto.port.RefundRequest;
import com.gp.dto.req.pay.PayInfo;
import com.gp.dto.req.pay.Refund;
import com.gp.dto.req.pay.RefundCommand;
import com.gp.dto.resp.pay.PayResponse;
import com.gp.dto.resp.pay.RefundResponse;
import com.gp.entity.TOrder;
import com.gp.entity.TPay;
import com.gp.exception.ServiceException;
import com.gp.handler.AliPayNativeHandler;
import com.gp.handler.AliRefundNativeHandler;
import com.gp.mapper.TOrderMapper;
import com.gp.mapper.TPayMapper;
import com.gp.service.TbPayAliService;
import com.gp.strategy.AbstractStrategyChoose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;


/**
 * 支付接口层实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TbPayAliServiceImpl implements TbPayAliService {


    private final TPayMapper payMapper;

    private final TOrderMapper orderMapper;

    private final  AbstractStrategyChoose abstractStrategyChoose;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public PayResponse commonPay(PayRequest requestParam) {
        /**
         * {@link AliPayNativeHandler}
         */
        // 策略模式：通过策略模式封装支付渠道和支付场景，用户支付时动态选择对应的支付组件
        PayResponse result = abstractStrategyChoose.chooseAndExecuteResp(requestParam.buildMark(), requestParam);
        TPay pay = BeanUtil.toBean(requestParam, TPay.class);
        String orderSn = requestParam.getOrderSn();
        pay.setOrderSn(orderSn);
        pay.setStatus(TradeStatusEnum.WAIT_BUYER_PAY.tradeCode());
        pay.setTotalAmount(requestParam.getTotalAmount());
        int insert = payMapper.insert(pay);
        if (insert <= 0) {
            log.error("支付单创建失败，支付聚合根：{}", JSON.toJSONString(requestParam));
            throw new ServiceException("支付单创建失败!");
        }
        return result;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void callbackPay(TPay tPay) {
        LambdaQueryWrapper<TPay> queryWrapper = Wrappers.lambdaQuery(TPay.class)
                .eq(TPay::getOrderSn, tPay.getOrderSn());
        TPay payDO = payMapper.selectOne(queryWrapper);
        if (Objects.isNull(payDO)) {
            log.error("支付单不存在，orderRequestId：{}", tPay.getTradeNo());
            throw new ServiceException("支付单不存在!");
        }
        payDO.setTradeNo(tPay.getTradeNo());
        payDO.setStatus(tPay.getStatus());
        payDO.setPayAmount(tPay.getPayAmount());
        payDO.setGmtPayment(tPay.getGmtPayment());
        LambdaUpdateWrapper<TPay> updateWrapper = Wrappers.lambdaUpdate(TPay.class)
                .eq(TPay::getOrderSn, tPay.getOrderSn());
        int result = payMapper.update(payDO, updateWrapper);
        if (result <= 0) {
            log.error("修改支付单支付结果失败，支付单信息：{}", JSON.toJSONString(payDO));
            throw new ServiceException("修改支付单支付结果失败!");
        }
        // 交易成功，回调订单服务告知支付结果，修改订单流转状态
        if (Objects.equals(tPay.getStatus(), TradeStatusEnum.TRADE_SUCCESS.tradeCode())) {
            TOrder order = new TOrder();
            order.setPayTime(payDO.getGmtPayment());
            order.setPayType(Integer.valueOf(payDO.getChannel()));
            order.setStatus(Integer.valueOf(payDO.getStatus()));
            LambdaUpdateWrapper<TOrder> wrapper = Wrappers.lambdaUpdate(TOrder.class)
                    .eq(TOrder::getOrderSn, payDO.getOrderSn());
            int updateResult = orderMapper.update(order, wrapper);
            if (updateResult <= 0) {
                throw new ServiceException("订单状态支付修改失败!");
            }
        }
    }

    @Override
    public PayInfo getPayInfoByOrderSn(String orderSn) {
        LambdaQueryWrapper<TPay> queryWrapper = Wrappers.lambdaQuery(TPay.class)
                .eq(TPay::getOrderSn, orderSn);
        TPay payDO = payMapper.selectOne(queryWrapper);
        return BeanUtil.toBean(payDO, PayInfo.class);
    }


    @Override
    public void commonRefund(Refund requestParam) {
        LambdaQueryWrapper<TPay> queryWrapper = Wrappers.lambdaQuery(TPay.class)
                .eq(TPay::getOrderSn, requestParam.getOrderSn());
        TPay payDO = payMapper.selectOne(queryWrapper);
        if (Objects.isNull(payDO)) {
            log.error("支付单不存在，orderSn：{}", requestParam.getOrderSn());
            throw new ServiceException("支付单不存在!");
        }
        /**
         * {@link AliRefundNativeHandler}
         */
        // 策略模式：通过策略模式封装退款渠道和退款场景，用户退款时动态选择对应的退款组件
        RefundCommand refundCommand = BeanUtil.toBean(payDO, RefundCommand.class);
        RefundRequest refundRequest = RefundRequestConvert.command2RefundRequest(refundCommand);
        RefundResponse result = abstractStrategyChoose.chooseAndExecuteResp(refundRequest.buildMark(), refundRequest);
        payDO.setStatus(result.getStatus());
        LambdaUpdateWrapper<TPay> updateWrapper = Wrappers.lambdaUpdate(TPay.class)
                .eq(TPay::getOrderSn, requestParam.getOrderSn());
        int updateResult = payMapper.update(payDO, updateWrapper);
        if (updateResult <= 0) {
            log.error("修改支付单退款结果失败，支付单信息：{}", JSON.toJSONString(payDO));
            throw new ServiceException("修改支付单退款结果失败!");
        }
    }
}
