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

package com.gp.convert;


import cn.hutool.core.bean.BeanUtil;
import com.gp.common.enums.PayChannelEnum;
import com.gp.dto.base.AliPayRequest;
import com.gp.dto.base.WxPayRequest;
import com.gp.dto.port.PayRequest;
import com.gp.dto.req.pay.PayCommand;


import java.util.Objects;

/**
 * 支付请求入参转换器
 */
public final class PayRequestConvert {

    /**
     * {@link PayCommand} to {@link PayRequest}
     *
     * @param payCommand 支付请求参数
     * @return {@link PayRequest}
     */
    public static PayRequest command2PayRequest(PayCommand payCommand) {
        PayRequest payRequest = null;
        if (Objects.equals(payCommand.getChannel(), PayChannelEnum.ALI_PAY.getCode())) {
            payRequest = BeanUtil.toBean(payCommand, AliPayRequest.class);
        }else if (Objects.equals(payCommand.getChannel(), PayChannelEnum.WX_PAY.getCode())){
            payRequest = BeanUtil.toBean(payCommand, WxPayRequest.class);
        }
        return payRequest;
    }
}
