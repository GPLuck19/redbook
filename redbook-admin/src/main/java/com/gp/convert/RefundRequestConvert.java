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
import com.gp.dto.base.AliRefundRequest;
import com.gp.dto.base.WxRefundRequest;
import com.gp.dto.port.RefundRequest;
import com.gp.dto.req.pay.RefundCommand;

import java.util.Objects;

/**
 * 退款请求入参转换器
 */
public final class RefundRequestConvert {

    /**
     * {@link RefundCommand} to {@link RefundRequest}
     *
     * @param refundCommand 退款请求参数
     * @return {@link RefundRequest}
     */
    public static RefundRequest command2RefundRequest(RefundCommand refundCommand) {
        RefundRequest refundRequest = null;
        if (Objects.equals(refundCommand.getChannel(), PayChannelEnum.ALI_PAY.getCode())) {
            refundRequest = BeanUtil.toBean(refundCommand, AliRefundRequest.class);
        }else if (Objects.equals(refundCommand.getChannel(), PayChannelEnum.WX_PAY.getCode())){
            refundRequest = BeanUtil.toBean(refundCommand, WxRefundRequest.class);
        }
        return refundRequest;
    }
}
