package com.gp.dto.resp.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long friendId;     // 好友ID
    private String username;   // 好友用户名
    private String realName;   // 好友昵称
    private String userPic;     // 好友头像
    private LocalDateTime addedTime; // 添加好友的时间
}
