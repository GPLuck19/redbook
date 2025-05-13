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

import com.gp.dto.base.EmailLoginRequest;
import com.gp.dto.base.SmsLoginRequest;
import com.gp.dto.base.UserLoginRequest;

/**
 * 登录请求入参
 */
public interface LoginRequest {



    /**
     * 获取登录请求回调入参
     */
    UserLoginRequest getUserLoginRequest();

    /**
     * 获取手机验证登录请求回调入参
     */
    SmsLoginRequest getMobileLoginRequest();

    /**
     * 获取邮箱验证登录请求回调入参
     */
    EmailLoginRequest getEmailLoginRequest();


}
