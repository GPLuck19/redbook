package com.gp.constant;

/**
 * websocket的常量配置
 */
public interface WebSocketConstants {

    /**
     * websocketSession中的参数的key
     */
    String LOGIN_USER_KEY = "loginUser";

    /**
     * 订阅的频道
     */
    String WEB_SOCKET_TOPIC = "global:websocket";

    /**
     * 用户信息
     */
    String LOGIN_USER_INFO_KEY = "loginUserInfo:";

    /**
     * 前端心跳检查的命令
     */
    String PING = "ping";

    /**
     * 服务端心跳恢复的字符串
     */
    String PONG = "pong";
}
