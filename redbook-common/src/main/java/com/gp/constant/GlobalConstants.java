package com.gp.constant;

/**
 * 全局的key常量 (业务无关的key)
 */
public interface GlobalConstants {

    /**
     * 全局 redis key (业务无关的key)
     */
    String GLOBAL_REDIS_KEY = "global:";

    /**
     * 验证码 redis key
     */
    String CAPTCHA_CODE_KEY = GLOBAL_REDIS_KEY + "captcha_codes:";

    /**
     * 防重提交 redis key
     */
    String REPEAT_SUBMIT_KEY = GLOBAL_REDIS_KEY + "repeat_submit:";

    /**
     * 限流 redis key
     */
    String RATE_LIMIT_KEY = GLOBAL_REDIS_KEY + "rate_limit:";

    /**
     * 三方认证 redis key
     */
    String SOCIAL_AUTH_CODE_KEY = GLOBAL_REDIS_KEY + "social_auth_codes:";

    /**
     * 登录日志 redis key
     */
    String LOGIN_LOG_KEY = GLOBAL_REDIS_KEY + "login_log";


    /**
     * 登操作日志 redis key
     */
    String OPERATION_LOG_KEY = GLOBAL_REDIS_KEY + "operation_log";

}
