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

package com.gp.common.enums;

/**
 * 购物相关责任链 Mark 枚举
 */
public enum GoodsChainMarkEnum {

    /**
     * 商品查询过滤器
     */
    GOODS_QUERY_FILTER,

    /**
     * 商品购买过滤器
     */
    GOODS_PURCHASE_FILTER,

    /**
     * 商品退款过滤器
     */
    GOODS_REFUND_FILTER
}
