package com.gp.controller.user;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.gp.dto.req.user.FriendMessageDao;
import com.gp.dto.req.user.UserSendMessageDao;
import com.gp.dto.resp.user.FriendMessageListVo;
import com.gp.entity.TUserMessage;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TUserMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 好友消息控制层
 */
@RestController
@RequiredArgsConstructor
public class UserFriendMessageController {

    private final TUserMessageService userMessageService;


    /**
     * 好友发送私信
     */
    @SaCheckDisable("sendMessage")
    @SaCheckPermission("/user/sendMessage")
    @PostMapping("/api/user-service/sendMessage")
    public Result sendMessage(@RequestBody UserSendMessageDao requestParam) {
        return Results.success(userMessageService.sendMessage(requestParam));
    }

    /**
     * 更新消息状态
     */
    @SaCheckPermission("/user/updateMessageStatus")
    @PostMapping("/api/user-service/updateMessageStatus")
    public Result updateMessageStatus(@RequestParam("messageId") Long messageId, @RequestParam("status") Integer status) {
        return Results.success(userMessageService.updateMessageStatus(messageId, status));
    }


    /**
     * 查看好友私信记录
     */
    @SaCheckPermission("/user/getChatHistory")
    @PostMapping("/api/user-service/getChatHistory")
    public Result<PageResponse<TUserMessage>> getChatHistory(@RequestBody FriendMessageDao requestParam) {
        return Results.success(userMessageService.getChatHistory(requestParam));
    }


    /**
     * 查看好友私信列表记录
     */
    @SaCheckPermission("/user/getFriendMessageList")
    @GetMapping("/api/user-service/getFriendMessageList")
    public Result<List<FriendMessageListVo>> getFriendMessageList(@RequestParam("userId") Long userId) {
        return Results.success(userMessageService.getFriendMessageList(userId));
    }

}
