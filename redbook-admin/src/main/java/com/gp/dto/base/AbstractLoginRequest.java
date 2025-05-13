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

package com.gp.dto.base;


import com.gp.dto.port.LoginRequest;
import lombok.Getter;
import lombok.Setter;
/**
 * 登录回调入参实体
 */
public abstract class AbstractLoginRequest implements LoginRequest {


    /**
     * 验证码
     */
    @Getter
    @Setter
    private String captcha;


    /**
     * 唯一用户判断
     */
    @Getter
    @Setter
    private String sessionKey;


    @Override
    public UserLoginRequest getUserLoginRequest() {
        return null;
    }

    @Override
    public SmsLoginRequest getMobileLoginRequest() {
        return null;
    }
    public EmailLoginRequest getEmailLoginRequest() {
        return null;
    }
}
