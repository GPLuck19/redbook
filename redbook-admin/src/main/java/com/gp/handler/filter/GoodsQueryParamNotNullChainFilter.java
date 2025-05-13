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

package com.gp.handler.filter;

import com.gp.dto.req.order.OrderDao;
import com.gp.exception.ClientException;
import com.gp.handler.GoodsQueryChainFilter;
import org.springframework.stereotype.Component;

/**
 * 查询物品流程过滤器之验证数据是否为空或空的字符串
 */
@Component
public class GoodsQueryParamNotNullChainFilter implements GoodsQueryChainFilter<OrderDao> {

    @Override
    public void handler(OrderDao requestParam) {
        if (requestParam.getQuantity() == 0) {
            throw new ClientException("订单数量不能为空");
        }
        if (requestParam.getGoodsId() == null) {
            throw new ClientException("商品号不能为空");
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
