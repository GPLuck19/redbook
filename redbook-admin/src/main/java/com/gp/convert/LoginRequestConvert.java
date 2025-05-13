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
import com.gp.common.enums.LoginEnum;
import com.gp.dto.port.LoginRequest;
import com.gp.dto.base.SmsLoginRequest;
import com.gp.dto.base.UserLoginRequest;
import com.gp.dto.req.user.UserLoginDao;

import java.util.Objects;

/**
 * 登录回调请求入参转换器
 */
public final class LoginRequestConvert {

    public static LoginRequest command2LoginRequest(UserLoginDao userLoginCommand) {
        LoginRequest loginRequest = null;
        if (Objects.equals(userLoginCommand.getGrantType(), LoginEnum.USER_NAME.getCode())) {
            loginRequest = BeanUtil.toBean(userLoginCommand, UserLoginRequest.class);
        } else if (Objects.equals(userLoginCommand.getGrantType(), LoginEnum.MOBILE_PHONE.getCode())) {
            loginRequest = BeanUtil.toBean(userLoginCommand, SmsLoginRequest.class);
        }
        return loginRequest;
    }
}
