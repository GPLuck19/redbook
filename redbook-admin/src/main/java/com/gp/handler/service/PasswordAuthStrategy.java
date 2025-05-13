package com.gp.handler.service;



import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.SmUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gp.dto.port.LoginRequest;
import com.gp.dto.base.UserLoginRequest;
import com.gp.dto.resp.user.FriendVo;
import com.gp.dto.resp.user.PermissionNode;
import com.gp.dto.resp.user.UserLoginVo;
import com.gp.entity.TUser;
import com.gp.enums.LoginType;
import com.gp.enums.UserEnum;
import com.gp.exception.ServiceException;
import com.gp.handler.LoginStrategy;
import com.gp.mapper.TRolePermissionsMapper;
import com.gp.mapper.TUserMapper;
import com.gp.mapper.TUserRolesMapper;
import com.gp.service.TPermissionsService;
import com.gp.service.UserLoginService;
import com.gp.userInfo.LoginUser;
import com.gp.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.gp.common.constant.UserRedisKey.IDENTIFYING_CODE_KEY;


/**
 * 密码认证策略
 */
@Slf4j
@Service("password" + LoginStrategy.BASE_NAME)
@RequiredArgsConstructor
public class PasswordAuthStrategy implements LoginStrategy {


    private final TUserMapper userMapper;

    private final TUserRolesMapper userRolesMapper;

    private final TRolePermissionsMapper rolePermissionsMapper;

    private final TPermissionsService permissionsService;

    private final UserLoginService loginService;


    @SneakyThrows
    @Override
    public UserLoginVo login(LoginRequest loginRequest) {
        UserLoginRequest requestParam = loginRequest.getUserLoginRequest();
        String userName = requestParam.getUserName();
        String code = requestParam.getCaptcha();
        String sessionKey = requestParam.getSessionKey();
        // 获取存储在 Redis 中的验证码
        String captcha = RedisUtils.getCacheObject(IDENTIFYING_CODE_KEY + sessionKey);

        if(captcha == null || !captcha.equals(code)){
            throw new ServiceException(UserEnum.USER_CODE_FAIL.getMsg());
        }
        String password=requestParam.getPassword();
        String decryptedPassword = RsaUtils.decryptByPrivateKey(password);
        //sm3与数据库对应
        String hashedPassword = SmUtil.sm3(decryptedPassword);
        LambdaQueryWrapper<TUser> queryWrapper = Wrappers.lambdaQuery(TUser.class)
                .eq(TUser::getUsername, userName)
                .eq(TUser::getPassword, hashedPassword)
                .select(TUser::getId, TUser::getUsername, TUser::getRealName,TUser::getUserPic);
        TUser userDO = userMapper.selectOne(queryWrapper);
        if (userDO != null) {
            LoginUser loginUser = buildLoginUser(userDO);
            // 生成token
            LoginHelper.login(loginUser);
            List<PermissionNode> userModules = permissionsService.getUserModules(userDO.getId());
            UserLoginVo actual = new UserLoginVo(loginUser.getUserId(), requestParam.getUserName(), userDO.getRealName(), StpUtil.getTokenValue(),userDO.getUserPic(),StpUtil.getTokenTimeout(),userModules);
            return actual;
        }else {
            loginService.checkLogin(LoginType.PASSWORD, userName, () -> true);
        }
        throw new ServiceException(UserEnum.SELECT_CURRENT_USER.getMsg());
    }


    /**
     * 构建登录用户
     */
    public LoginUser buildLoginUser(TUser user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setNickname(user.getRealName());
        // 获取用户的角色ID列表
        List<Long> roleIds = userRolesMapper.getRoleIdsByUserId(user.getId());
        // 获取角色对应的权限ID列表
        List<String> permissionPath = rolePermissionsMapper.getPermissionPathByRoleIds(roleIds);
        loginUser.setMenuPermission(permissionPath);
        return loginUser;
    }


}
