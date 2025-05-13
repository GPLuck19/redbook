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

package com.gp.controller.user;


import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSONObject;
import com.gp.annotation.RepeatSubmit;
import com.gp.constant.Constants;
import com.gp.constant.GlobalConstants;
import com.gp.convert.LoginRequestConvert;
import com.gp.dto.port.LoginRequest;
import com.gp.dto.req.user.UserLoginDao;
import com.gp.dto.resp.user.UserLoginVo;
import com.gp.handler.LoginStrategy;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.EmailService;
import com.gp.service.UserLoginService;
import com.gp.utils.RedisUtils;
import com.gp.utils.SseMessageUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 用户登录控制层
 */
@RestController
@RequiredArgsConstructor
@SaIgnore
public class UserLoginController {


    private final UserLoginService userLoginService;

    private final ScheduledExecutorService scheduledExecutorService;

    private final EmailService mailService;


    /**
     * 用户登录
     */
    @PostMapping("/api/user-service/login")
    public Result<UserLoginVo> login(@RequestBody UserLoginDao requestParam) {
        LoginRequest loginRequest = LoginRequestConvert.command2LoginRequest(requestParam);
        UserLoginVo loginVo = LoginStrategy.login(loginRequest, requestParam.getGrantType());
        scheduledExecutorService.schedule(() -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("receiverId", loginVo.getUserId());
            jsonObject.put("message", "欢迎登录RedBook");
            jsonObject.put("messageType", "SYSTEM");
            SseMessageUtils.sendMessage(jsonObject.toJSONString());
        }, 3, TimeUnit.SECONDS);
        return Results.success(loginVo);
    }

    /**
     * 短信验证码
     *
     * @param phonenumber 用户手机号
     */
    @RepeatSubmit(interval = 60, timeUnit = TimeUnit.SECONDS, message = "{repeat.submit.message}")
    @GetMapping("/api/user-service/smsCode")
    public Result<Void> smsCode(@NotBlank(message = "{user.phonenumber.not.blank}") String phonenumber) {
        String key = GlobalConstants.CAPTCHA_CODE_KEY + phonenumber;
        String code = RandomUtil.randomNumbers(4);
        RedisUtils.setCacheObject(key, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));
        // 验证码模板id 自行处理 (查数据库或写死均可)
        String templateId = "";
        LinkedHashMap<String, String> map = new LinkedHashMap<>(1);
        map.put("code", code);
        SmsBlend smsBlend = SmsFactory.getSmsBlend("config1");
        SmsResponse smsResponse = smsBlend.sendMessage(phonenumber, templateId, map);
        if (!smsResponse.isSuccess()) {
            return Results.failure();
        }
        return Results.success();
    }

    /**
     * 邮箱验证码
     *
     * @param email 邮箱
     */
    @RepeatSubmit(interval = 60, timeUnit = TimeUnit.SECONDS, message = "{repeat.submit.message}")
    @GetMapping("/api/user-service/emailCode")
    public Result<Void> emailCode(@NotBlank(message = "{user.email.not.blank}") String email) {
        String key = GlobalConstants.CAPTCHA_CODE_KEY + email;
        String code = RandomUtil.randomNumbers(4);
        RedisUtils.setCacheObject(key, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));
        mailService.sendSimpleMail(email,"主题：你好普通邮件","内容：你登录的验证码账号为："+code+" ,2分钟有效期!");
        return Results.success();
    }


    /**
     * 获取登录验证码
     */
    @GetMapping("/api/user-service/captcha")
    public Result<String> generateCaptcha(@RequestParam(name = "sessionKey") String sessionKey) {
        return Results.success(userLoginService.generateCaptcha(sessionKey));
    }


    /**
     * 用户退出登录
     */
    @GetMapping("/api/user-service/logout")
    public Result logout() {
        return userLoginService.logout();
    }

}
