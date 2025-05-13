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

package com.gp.controller.pay;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import com.gp.common.enums.PayChannelEnum;
import com.gp.convert.PayCallbackRequestConvert;
import com.gp.convert.PayRequestConvert;
import com.gp.dto.port.PayCallbackRequest;
import com.gp.dto.port.PayRequest;
import com.gp.dto.req.pay.PayCallbackCommand;
import com.gp.dto.req.pay.PayCommand;
import com.gp.dto.req.pay.PayInfo;
import com.gp.dto.req.pay.Refund;
import com.gp.dto.resp.pay.PayResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TbPayAliService;
import com.gp.strategy.AbstractStrategyChoose;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 支付控制层
 */
@RestController
@RequiredArgsConstructor
public class AliPayController {

    private final TbPayAliService tbPayAliService;

    private final AbstractStrategyChoose abstractStrategyChoose;

    /**
     * 公共支付接口
     * 对接常用支付方式，比如：支付宝、微信以及银行卡等
     */
    @PostMapping("/api/ali-pay/create")
    public Result<PayResponse> pay(@RequestBody PayCommand requestParam) {
        PayRequest payRequest = PayRequestConvert.command2PayRequest(requestParam);
        return Results.success(tbPayAliService.commonPay(payRequest));
    }

    /**
     * 跟据订单号查询支付单详情
     */
    @GetMapping("/api/ali-pay/query/order-sn")
    public Result<PayInfo> getPayInfoByOrderSn(@RequestParam(value = "orderSn") String orderSn) {
        return Results.success(tbPayAliService.getPayInfoByOrderSn(orderSn));
    }


    /**
     * 公共退款接口
     */
    @Deprecated
    @PostMapping("/api/ali-pay/refund")
    public void refund(@RequestBody Refund requestParam) {
        tbPayAliService.commonRefund(requestParam);
    }


    /**
     * 支付宝回调
     * 调用支付宝支付后，支付宝会调用此接口发送支付结果
     */
    @PostMapping("/api/ali-pay/callback/alipay")
    public void callbackAlipay(@RequestParam Map<String, Object> requestParam) {
        PayCallbackCommand payCallbackCommand = BeanUtil.mapToBean(requestParam, PayCallbackCommand.class, true, CopyOptions.create());
        payCallbackCommand.setChannel(PayChannelEnum.ALI_PAY.getCode());
        payCallbackCommand.setGmtPayment(DateUtil.parse(requestParam.get("gmt_payment").toString()));
        PayCallbackRequest payCallbackRequest = PayCallbackRequestConvert.command2PayCallbackRequest(payCallbackCommand);
        abstractStrategyChoose.chooseAndExecute(payCallbackRequest.buildMark(), payCallbackRequest);
    }
}
