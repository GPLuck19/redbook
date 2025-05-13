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

package com.gp.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.gp.config.UserPasswordProperties;
import com.gp.constant.UserConstant;
import com.gp.enums.LoginType;
import com.gp.exception.ClientException;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.UserLoginService;
import com.gp.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;
import java.util.function.Supplier;

import static com.gp.common.constant.UserRedisKey.IDENTIFYING_CODE_KEY;


/**
 * 用户登录接口实现
 */
@Slf4j
@Service
public class UserLoginServiceImpl implements UserLoginService {


    @Autowired
    private UserPasswordProperties userPasswordProperties;



    public void checkLogin(LoginType loginType, String username, Supplier<Boolean> supplier) {
        String errorKey = UserConstant.PWD_ERR_CNT_KEY + username;
        Integer maxRetryCount = userPasswordProperties.getMaxRetryCount();
        Integer lockTime = userPasswordProperties.getLockTime();

        // 获取用户登录错误次数，默认为0 (可自定义限制策略 例如: key + username + ip)
        int errorNumber = ObjectUtil.defaultIfNull(RedisUtils.getCacheObject(errorKey), 0);
        // 锁定时间内登录 则踢出
        if (errorNumber >= maxRetryCount) {
            throw new ClientException("重试次数过多，账号已被锁定!");
        }

        if (supplier.get()) {
            // 错误次数递增
            errorNumber++;
            RedisUtils.setCacheObject(errorKey, errorNumber, Duration.ofMinutes(lockTime));
            // 达到规定错误次数 则锁定登录
            if (errorNumber >= maxRetryCount) {
                throw new ClientException("重试次数过多，账号已被锁定!");
            }
        }
        // 登录成功 清空错误次数
        RedisUtils.deleteObject(errorKey);
    }




    @Override
    public Result logout() {
        StpUtil.logout();
        return Results.success();
    }

    @Override
    public String generateCaptcha(String sessionKey) {
        String verifyKey = IDENTIFYING_CODE_KEY + sessionKey;
        // 生成 4 位数字验证码
        String captcha = String.format("%04d", new Random().nextInt(10000));
        RedisUtils.setCacheObject(verifyKey,captcha,Duration.ofSeconds(60));
        return captcha;
    }
}
