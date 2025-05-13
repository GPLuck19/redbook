package com.gp.handler;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.gp.dto.WebSocketMessageDto;
import com.gp.holder.WebSocketSessionHolder;
import com.gp.userInfo.LoginUser;
import com.gp.utils.RedisUtils;
import com.gp.utils.WebSocketUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.gp.constant.WebSocketConstants.LOGIN_USER_INFO_KEY;
import static com.gp.constant.WebSocketConstants.LOGIN_USER_KEY;


/**
 * WebSocketHandler 实现类g
 */
@Slf4j
public class PlusWebSocketHandler extends AbstractWebSocketHandler {

    /**静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。*/
    private static int onlineCount = 0;

    /**
     * 连接成功后
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        LoginUser loginUser = (LoginUser) session.getAttributes().get(LOGIN_USER_KEY);
        if (ObjectUtil.isNull(loginUser)) {
            session.close(CloseStatus.BAD_DATA);
            log.info("[connect] invalid token received. sessionId: {}", session.getId());
            return;
        }
        RedisUtils.setCacheObject(LOGIN_USER_INFO_KEY+loginUser.getUserId(), loginUser, Duration.ofMinutes(30));
        WebSocketSessionHolder.addSession(loginUser.getUserId(), session);
        addOnlineCount();
        log.info("[connect] sessionId: {},userId:{}", session.getId(), loginUser.getUserId());
        log.info("用户连接ID: "+loginUser.getUserId()+",当前在线人数为: " + getOnlineCount());
    }

    /**
     * 处理接收到的文本消息
     *
     * @param session WebSocket会话
     * @param message 接收到的文本消息
     * @throws Exception 处理消息过程中可能抛出的异常
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 从WebSocket会话中获取登录用户信息
        LoginUser loginUser = (LoginUser) session.getAttributes().get(LOGIN_USER_KEY);
        JSONObject jsonMessage = JSONObject.parseObject(message.getPayload());
        String type = jsonMessage.getString("type");
        WebSocketMessageDto webSocketMessageDto = new WebSocketMessageDto();
        switch (type) {
            case "get_user_states":
                //返回所有用户在线或者离线数据
                webSocketMessageDto.setSessionKeys(List.of(loginUser.getUserId()));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type","user_states");
                jsonObject.put("states",WebSocketSessionHolder.getSessionsAll());
                //查询在线用户信息
                // 获取所有的在线用户键（以 online_user: 为前缀）
                Collection<String> keys = RedisUtils.scanKeys("loginUserInfo:*");
                // 获取每个键对应的用户信息
                List<LoginUser> users = keys.stream()
                        .map(key -> {
                            // 获取缓存中的对象并进行类型检查
                            Object obj = RedisUtils.getCacheObject(key);
                            if (obj instanceof LoginUser) {
                                return (LoginUser) obj;  // 强制转换为 LoginUser 类型
                            }
                            return null;  // 如果不是 LoginUser 类型，则返回 null
                        })
                        .filter(user -> user != null)  // 过滤掉 null 值
                        .collect(Collectors.toList());  // 收集成一个列表
                jsonObject.put("userInfo",users);
                webSocketMessageDto.setMessage(jsonObject.toJSONString());
                break;
            default:
                log.warn("未知消息类型: " + type);
        }
        // 创建WebSocket消息DTO对象
        WebSocketUtils.publishMessage(webSocketMessageDto);
    }

    /**
     * 处理接收到的二进制消息
     *
     * @param session WebSocket会话
     * @param message 接收到的二进制消息
     * @throws Exception 处理消息过程中可能抛出的异常
     */
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        super.handleBinaryMessage(session, message);
    }

    /**
     * 处理接收到的Pong消息（心跳监测）
     *
     * @param session WebSocket会话
     * @param message 接收到的Pong消息
     * @throws Exception 处理消息过程中可能抛出的异常
     */
    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        WebSocketUtils.sendPongMessage(session);
    }

    /**
     * 处理WebSocket传输错误
     *
     * @param session   WebSocket会话
     * @param exception 发生的异常
     * @throws Exception 处理过程中可能抛出的异常
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("[transport error] sessionId: {} , exception:{}", session.getId(), exception.getMessage());
    }

    /**
     * 在WebSocket连接关闭后执行清理操作
     *
     * @param session WebSocket会话
     * @param status  关闭状态信息
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        LoginUser loginUser = (LoginUser) session.getAttributes().get(LOGIN_USER_KEY);
        if (ObjectUtil.isNull(loginUser)) {
            log.info("[disconnect] invalid token received. sessionId: {}", session.getId());
            return;
        }
        WebSocketSessionHolder.removeSession(loginUser.getUserId());
        RedisUtils.deleteObject(LOGIN_USER_INFO_KEY+loginUser.getUserId());
        subOnlineCount();
        log.info("[disconnect] sessionId: {},userId:{}", session.getId(), loginUser.getUserId());
        log.info("用户【"+loginUser.getUserId()+"】退出: 当前在线人数为: " + getOnlineCount());
    }

    /**
     * 指示处理程序是否支持接收部分消息
     *
     * @return 如果支持接收部分消息，则返回true；否则返回false
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


    /**
     * 获取当前在线人数
     * @return
     */
    public synchronized int getOnlineCount() {
        return onlineCount;
    }

    /**
     * 在线人数加1
     */
    public synchronized void addOnlineCount() {
        PlusWebSocketHandler.onlineCount++;
    }

    /**
     * 在线人数减1
     */
    public synchronized void subOnlineCount() {
        PlusWebSocketHandler.onlineCount--;
    }

}
