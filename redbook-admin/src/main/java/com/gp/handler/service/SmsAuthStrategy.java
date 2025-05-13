package com.gp.handler.service;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gp.constant.GlobalConstants;
import com.gp.dto.port.LoginRequest;
import com.gp.dto.base.SmsLoginRequest;
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
import com.gp.utils.LoginHelper;
import com.gp.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 短信认证策略
 *
 * @author Michelle.Chung
 */
@Slf4j
@Service("sms" + LoginStrategy.BASE_NAME)
@RequiredArgsConstructor
public class SmsAuthStrategy implements LoginStrategy {


    private final TUserMapper userMapper;

    private final TUserRolesMapper userRolesMapper;

    private final TRolePermissionsMapper rolePermissionsMapper;

    private final TPermissionsService permissionsService;

    /**
     * 校验短信验证码
     */
    private boolean validateSmsCode( String phone, String smsCode) {
        String code = RedisUtils.getCacheObject(GlobalConstants.CAPTCHA_CODE_KEY + phone);
        return code.equals(smsCode);
    }

    @Override
    public UserLoginVo login(LoginRequest loginRequest) {
        SmsLoginRequest mobileLoginRequest = loginRequest.getMobileLoginRequest();
        String phone = mobileLoginRequest.getPhone();
        String smsCode = mobileLoginRequest.getCode();
        boolean validateSmsCode = validateSmsCode(phone, smsCode);
        if(!validateSmsCode){
            throw new ServiceException(UserEnum.USER_CODE_FAIL.getMsg());
        }
        LambdaQueryWrapper<TUser> queryWrapper = Wrappers.lambdaQuery(TUser.class)
                .eq(TUser::getPhone,phone);
        TUser userDO = userMapper.selectOne(queryWrapper);
        if (userDO != null) {
            LoginUser loginUser = buildLoginUser(userDO);
            // 生成token
            LoginHelper.login(loginUser);
            List<PermissionNode> userModules = permissionsService.getUserModules(userDO.getId());
            UserLoginVo actual = new UserLoginVo(loginUser.getUserId(), userDO.getUsername(), userDO.getRealName(), StpUtil.getTokenValue(),userDO.getUserPic(),StpUtil.getTokenTimeout(),userModules);
            return actual;
        }
        return null;
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
