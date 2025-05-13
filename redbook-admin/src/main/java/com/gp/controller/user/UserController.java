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



import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.gp.annotation.Log;
import com.gp.dto.req.user.UserListDao;
import com.gp.dto.req.user.UserLockDao;
import com.gp.dto.req.user.UserRegisterDao;
import com.gp.dto.req.user.UserUpdateDao;
import com.gp.dto.resp.user.UserInfo;
import com.gp.dto.resp.user.UserRegisterVo;
import com.gp.entity.TUser;
import com.gp.enums.OperatorType;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TUserService;
import com.gp.userInfo.SysUserOnline;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户控制层
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final TUserService userService;

    /**
     * 获取用户信息
     */
    /*@SaCheckPermission("/user/getUserInfoById")*/
    @SaIgnore
    @GetMapping("/api/user-service/getUserInfoById")
    public Result<UserInfo> getUserInfoById(@RequestParam("userId") Long userId) {
        return Results.success(userService.getUserInfoById(userId));
    }


    /**
     * 批量获取用户信息
     */
    @SaCheckPermission("/user/getUserInfoByIds")
    @PostMapping("/api/user-service/getUserInfoByIds")
    public Result<Map<Long, UserInfo>> getUserInfoByIds(@RequestBody Set<Long> userIds) {
        return Results.success(userService.getUserInfoByIds(userIds));
    }


    /**
     * 更换头像
     */
    @SaCheckPermission("/user/updateAvatar")
    @PostMapping("/api/user-service/updateAvatar")
    public Result updateAvatar(@RequestParam("userId") Long userId,@RequestParam("avatar") String avatar) {
        return Results.success(userService.updateAvatar(userId,avatar));
    }


    /**
     * 更换背景图片
     */
    @SaCheckPermission("/user/updateBackground")
    @PostMapping("/api/user-service/updateBackground")
    public Result updateBackgroud(@RequestParam("userId") Long userId,@RequestParam("background") String background) {
        return Results.success(userService.updateBackground(userId,background));
    }

    /**
     * 修改用户
     */
    @Log(title = "修改用户", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/updateUserById")
    @PostMapping("/api/user-service/updateUserById")
    public Result updateUserById(@RequestBody UserUpdateDao requestParam) {
        return Results.success(userService.updateUserById(requestParam));
    }

    /**
     * 注册用户
     */
    @PostMapping("/api/user-service/register")
    @SaIgnore
    public Result<UserRegisterVo> register(@RequestBody UserRegisterDao requestParam) {
        return Results.success(userService.register(requestParam));
    }

    /**
     * 获取用户列表信息
     */
    @Log(title = "获取用户信息", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/userList")
    @PostMapping("/api/user-service/userList")
    public Result<PageResponse<TUser>> userList(@RequestBody UserListDao requestParam) {
        return Results.success(userService.userList(requestParam));
    }



    /**
     * 封禁用户
     */
    @Log(title = "封禁用户", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/lockUser")
    @PostMapping("/api/user-service/lockUser")
    public Result lockUser(@RequestBody UserLockDao requestParam) {
        return Results.success(userService.lockUser(requestParam));
    }

    /**
     * 踢出用户
     */
    @GetMapping("/api/user-service/kickout")
    @SaCheckPermission("/user/kickout")
    public Result kickout(@RequestParam("userId") Long userId) {
        return userService.kickout(userId);
    }


    /**
     * 设备管理
     */
    @GetMapping("/api/user-service/equipmentManagement")
    public Result<List<SysUserOnline>> equipmentManagement() {
        return Results.success(userService.equipmentManagement());
    }


    /**
     * 移除设备
     */
    @GetMapping("/api/user-service/removeDevice")
    public Result removeDevice(@RequestParam("tokenId") String tokenId) {
        return userService.removeDevice(tokenId);
    }

}
