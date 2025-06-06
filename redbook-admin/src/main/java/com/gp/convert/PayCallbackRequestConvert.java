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
import com.gp.dto.base.AliPayCallbackRequest;
import com.gp.dto.base.WxPayCallbackRequest;
import com.gp.dto.port.PayCallbackRequest;
import com.gp.dto.req.pay.PayCallbackCommand;

import java.util.Objects;

/**
 * 支付回调请求入参转换器
 */
public final class PayCallbackRequestConvert {

    /**
     * {@link PayCallbackCommand} to {@link PayCallbackRequest}
     *
     * @param payCallbackCommand 支付回调请求参数
     * @return {@link PayCallbackRequest}
     */
    public static PayCallbackRequest command2PayCallbackRequest(PayCallbackCommand payCallbackCommand) {
        PayCallbackRequest payCallbackRequest = null;
        if (Objects.equals(payCallbackCommand.getChannel(), PayChannelEnum.ALI_PAY.getCode())) {
            payCallbackRequest = BeanUtil.toBean(payCallbackCommand, AliPayCallbackRequest.class);
        }else if (Objects.equals(payCallbackCommand.getChannel(), PayChannelEnum.WX_PAY.getCode())){
            payCallbackRequest = BeanUtil.toBean(payCallbackCommand, WxPayCallbackRequest.class);
        }
        return payCallbackRequest;
    }
}
