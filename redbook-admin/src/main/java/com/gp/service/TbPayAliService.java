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

package com.gp.service;


import com.gp.dto.port.PayRequest;
import com.gp.dto.req.pay.PayInfo;
import com.gp.dto.req.pay.Refund;
import com.gp.dto.resp.pay.PayResponse;
import com.gp.entity.TPay;

/**
 * 支付接口层
 */
public interface TbPayAliService {

    /**
     * 创建支付单
     *
     * @param requestParam 创建支付单实体
     * @return 支付返回详情
     */
    PayResponse commonPay(PayRequest requestParam);

    /**
     * 支付单回调
     *
     * @param tPay 回调支付单实体
     */
    void callbackPay(TPay tPay);

    /**
     * 跟据订单号查询支付单详情
     *
     * @param orderSn 订单号
     * @return 支付单详情
     */
    PayInfo getPayInfoByOrderSn(String orderSn);



    /**
     * 公共退款接口
     *
     * @param requestParam 退款请求参数
     * @return 退款返回详情
     */
    void commonRefund(Refund requestParam);
}
