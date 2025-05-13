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

package com.gp.common.constant;

/**
 * Redis Key 定义常量类
 */
public final class GoodsRedisKey {

    /**
     * 所有商品key
     */
    public static final String GOODS_LIST_KEY = "goods-service:query_all_goods_list";

    /**
     * 所有商品锁
     */
    public static final String GOODS_LOCK_LIST_KEY = "goods-service:lock:query_all_goods_list";

    /**
     * 用户购买分布式锁
     */
    public static final String GOODS_LOCK_PAY_KEY = "goods-service:pay:lock";

    /**
     * 商品基本信息
     */
    public static final String GOODS_INFO = "goods-service:info:";


}
