package com.gp.handler;

import com.gp.dto.port.LoginRequest;
import com.gp.dto.resp.user.UserLoginVo;
import com.gp.exception.ServiceException;
import com.gp.utils.SpringUtils;

/**
 * 登录授权策略
 */
public interface LoginStrategy {

    String BASE_NAME = "AuthStrategy";

    /**
     * 登录
     * @param grantType 授权类型
     * @return 登录验证信息
     */
    static UserLoginVo login(LoginRequest loginRequest, String grantType) {
        // 授权类型和客户端id
        String beanName = grantType + BASE_NAME;
        if (!SpringUtils.containsBean(beanName)) {
            throw new ServiceException("授权类型不正确!");
        }
        LoginStrategy instance = SpringUtils.getBean(beanName);
        return instance.login(loginRequest);
    }

    /**
     * 登录
     * @return 登录验证信息
     */
    UserLoginVo login(LoginRequest loginRequest);

}
