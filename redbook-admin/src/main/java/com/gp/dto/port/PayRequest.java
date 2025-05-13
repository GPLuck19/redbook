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

package com.gp.dto.port;

import com.gp.dto.base.AliPayRequest;
import com.gp.dto.base.WxPayRequest;

import java.math.BigDecimal;

/**
 * 支付入参接口
 */
public interface PayRequest {

    /**
     * 获取阿里支付入参
     */
    AliPayRequest getAliPayRequest();
    WxPayRequest getWxPayRequest();
    /**
     * 获取订单号
     */
    String getOrderSn();

    BigDecimal getTotalAmount();

    /**
     * 构建查找支付策略实现类标识
     */
    String buildMark();
}
