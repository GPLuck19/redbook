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
public final class UserRedisKey {

    /**
     * 用户注册锁，Key Prefix + 用户名
     */
    public static final String LOCK_USER_REGISTER = "user-service:lock:user-register:";

    /**
     * 用户注销锁，Key Prefix + 用户名
     */
    public static final String USER_DELETION = "user-service:user-deletion:";


    /**
     * 用户登录，Key Prefix + 用户名
     */
    public static final String USER_LOGIN = "user-service:user-login:";


    /**
     * 用户注册可复用用户名分片，Key Prefix + Idx
     */
    public static final String USER_REGISTER_REUSE_SHARDING = "user-service:user-reuse:";


    /**
     * 用户验证码
     */
    public static final String IDENTIFYING_CODE_KEY = "IDENTIFYING_CODE_KEY:";


    /**
     * 用户验证码
     */
    public static final String USER_LOCK_KEY = "USER_LOCK_KEY:";


    //默认redis等待时间
    public static final int REDIS_WAIT_TIME = 5;

    //默认redis自动释放时间
    public static final int REDIS_LEASETIME = 4;

    /**
     * 用户地址信息
     */
    public static final String USER_ADDRESS_INFO = "user_address_info:";





}
