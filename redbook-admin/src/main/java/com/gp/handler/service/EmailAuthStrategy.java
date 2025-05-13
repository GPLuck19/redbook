package com.gp.handler.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gp.constant.GlobalConstants;
import com.gp.dto.base.EmailLoginRequest;
import com.gp.dto.base.SmsLoginRequest;
import com.gp.dto.port.LoginRequest;
import com.gp.dto.resp.user.PermissionNode;
import com.gp.dto.resp.user.UserLoginVo;
import com.gp.entity.TUser;
import com.gp.enums.UserEnum;
import com.gp.exception.ServiceException;
import com.gp.handler.LoginStrategy;
import com.gp.mapper.TRolePermissionsMapper;
import com.gp.mapper.TUserMapper;
import com.gp.mapper.TUserRolesMapper;
import com.gp.service.TPermissionsService;
import com.gp.userInfo.LoginUser;
import com.gp.utils.LoginHelper;
import com.gp.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 邮件认证策略
 *
 */
@Slf4j
@Service("email" + LoginStrategy.BASE_NAME)
@RequiredArgsConstructor
public class EmailAuthStrategy implements LoginStrategy {


    private final TUserMapper userMapper;

    private final TUserRolesMapper userRolesMapper;

    private final TRolePermissionsMapper rolePermissionsMapper;

    private final TPermissionsService permissionsService;

    /**
     * 校验短信验证码
     */
    private boolean validatEmailCode( String email, String emailCode) {
        String code = RedisUtils.getCacheObject(GlobalConstants.CAPTCHA_CODE_KEY + email);
        return code.equals(emailCode);
    }

    @Override
    public UserLoginVo login(LoginRequest loginRequest) {
        EmailLoginRequest emailLoginRequest = loginRequest.getEmailLoginRequest();
        String email = emailLoginRequest.getEmail();
        String emailCode = emailLoginRequest.getCode();
        boolean validateSmsCode = validatEmailCode(email, emailCode);
        if(!validateSmsCode){
            throw new ServiceException(UserEnum.USER_CODE_FAIL.getMsg());
        }
        LambdaQueryWrapper<TUser> queryWrapper = Wrappers.lambdaQuery(TUser.class)
                .eq(TUser::getMail,email);
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
