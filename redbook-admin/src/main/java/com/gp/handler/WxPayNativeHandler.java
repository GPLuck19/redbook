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

package com.gp.handler;

import cn.hutool.core.text.StrBuilder;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.service.WxPayService;
import com.gp.common.enums.PayChannelEnum;
import com.gp.common.enums.PayTradeTypeEnum;
import com.gp.dto.base.WxPayRequest;
import com.gp.dto.port.PayRequest;
import com.gp.dto.resp.pay.PayCodeUrlResult;
import com.gp.dto.resp.pay.PayResponse;
import com.gp.enums.OrderEnum;
import com.gp.exception.ServiceException;
import com.gp.handler.base.AbstractWxPayHandler;
import com.gp.strategy.AbstractExecuteStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.InetAddress;

/**
 * 阿里支付组件
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WxPayNativeHandler extends AbstractWxPayHandler implements AbstractExecuteStrategy<PayRequest, PayResponse> {

    private final WxPayService wxPayService;


    @Override
    public PayResponse pay(PayRequest payRequest) {
        WxPayRequest wxPayRequest = payRequest.getWxPayRequest();
        WxPayUnifiedOrderResult result = null;
        try {
            // 4.和第三方支付系统交互获得支付的链接地址
            // 1.创建统一下单的请求对象
            WxPayUnifiedOrderRequest unifiedOrderRequest = new WxPayUnifiedOrderRequest();

            // 2.设置统一下单数据
            // 商品描述
            unifiedOrderRequest.setBody("订单名称:"+ wxPayRequest.getSubject());
            // 商品订单号
            unifiedOrderRequest.setOutTradeNo(wxPayRequest.getOrderSn());

            BigDecimal price = wxPayRequest.getTotalAmount();
            Integer yuanToFen = WxPayUnifiedOrderRequest.yuanToFen(price.toString());
            // 商品金额（单位为：分）
            unifiedOrderRequest.setTotalFee(yuanToFen);
            // 生成二维码的终端IP地址
            unifiedOrderRequest.setSpbillCreateIp(InetAddress.getLocalHost().getHostAddress());
            // 订单的详情描述
            unifiedOrderRequest.setDetail("购买订单id："+ wxPayRequest.getOrderSn()+" 商品信息："+ wxPayRequest.getSubject());
            // 订单的商品Id号
            unifiedOrderRequest.setProductId(wxPayRequest.getGoodsId());
            // 3.调用service获得生成二维码的结果数据
            result = wxPayService.unifiedOrder(unifiedOrderRequest);
        } catch (Exception e) {
            log.error(OrderEnum.ORDER_PAY_WX_BAD.getMsg()+" error msg:{}",e.getMessage());
            throw new ServiceException("微信支付接口调用失败！");
        }


        System.out.println("-----------------获得结果信息--------------------");

        String success_flag = PayCodeUrlResult.WX_PAY_SUCCESS_FLAG;

        PayCodeUrlResult codeUrlResult = null;
        if (success_flag.equals(result.getReturnCode()) && success_flag.equals(result.getResultCode())) {
            // 获得二维码路径
            String codeURL = result.getCodeURL();
            codeUrlResult = PayCodeUrlResult.success(codeURL);

        } else {
            codeUrlResult = PayCodeUrlResult.failed(result.getErrCode()+" ::   "+result.getErrCodeDes());
        }
        return new PayResponse(codeUrlResult.toString());
    }

    @Override
    public String mark() {
        return StrBuilder.create()
                .append(PayChannelEnum.WX_PAY.name())
                .append("_")
                .append(PayTradeTypeEnum.NATIVE.name())
                .toString();
    }

    @Override
    public PayResponse executeResp(PayRequest requestParam) {
        return pay(requestParam);
    }
}
