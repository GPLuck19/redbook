package com.gp.dto.req.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDao {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    private String sessionKey;

    private String captcha;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 授权类型
     */
    private String grantType;

}
