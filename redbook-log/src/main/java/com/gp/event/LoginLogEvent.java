package com.gp.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录事件
 */

@Data
public class LoginLogEvent implements Serializable {


    /**
     * 用户账号
     */
    private String username;

    /**
     * 登录状态 0成功 1失败
     */
    private String status;

    /**
     * 提示消息
     */
    private String message;

    /**
     * 其他参数
     */
    private Object[] args;

    /**
     * 请求体
     */
    private HttpServletRequest request;

}
