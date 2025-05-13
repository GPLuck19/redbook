package com.gp.dto.resp.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendMessageListVo {
    /**
     * 好友的用户ID
     */
    private Long friendId;

    /**
     * 好友的用户名
     */
    private String username;

    /**
     * 好友的真实姓名
     */
    private String realName;

    /**
     * 好友的头像URL
     */
    private String userPic;

    /**
     * 最新一条私信的内容
     */
    private String lastMessage;

    /**
     * 最新一条私信的时间
     */
    private LocalDateTime lastMessageTime;

    private Integer messageType;
}
