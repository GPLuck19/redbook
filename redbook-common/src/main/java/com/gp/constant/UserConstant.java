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

package com.gp.constant;

/**
 * 用户常量
 */
public final class UserConstant {

    /**
     * 用户 ID Key
     */
    public static final String USER_ID_KEY = "userId";

    /**
     * 用户名 Key
     */
    public static final String USER_NAME_KEY = "username";

    /**
     * 用户真实名称 Key
     */
    public static final String REAL_NAME_KEY = "realName";

    /**
     * 用户 Token Key
     */
    public static final String USER_TOKEN_KEY = "token";


    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";

    /**
     * 在线用户 redis key
     */
    public static final String ONLINE_TOKEN_KEY = "online_tokens:";

    /**
     * 用户数据批量缓存 redis key
     */
    public static final String USER_INFO_KEY = "userInfo:";
}
