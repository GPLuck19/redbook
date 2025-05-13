package com.gp.controller.ai;

import cn.dev33.satoken.annotation.SaIgnore;
import com.gp.dto.req.ai.AiMessageInput;
import com.gp.dto.req.ai.AiSession;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TAiMessageService;
import com.gp.service.TAiSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SaIgnore
public class AIMessageController {

    private final TAiSessionService aiSessionService;

    private final TAiMessageService aiMessageService;


    // 创建新对话
    @PostMapping("/api/ai/conversation")
    public Result createConversation(@RequestBody AiSession aiSession) {
        return Results.success(aiSessionService.createConversation(aiSession));
    }

    //刪除对话
    @DeleteMapping("/api/ai/deleteConversation")
    public Result deleteConversation(@RequestParam("sessionId") Long sessionId) {
        return Results.success(aiSessionService.deleteConversation(sessionId));
    }

    // 查询当前用户的会话列表
    @GetMapping("/api/ai/getConversationById")
    public Result getConversationById(@RequestParam("userId") Long userId) {
        return Results.success(aiSessionService.getConversationById(userId));
    }

    // 根据会话id查询消息列表
    @GetMapping("/api/ai/getMessagesId")
    public Result getMessagesId(@RequestParam("sessionId") Long sessionId) {
        return Results.success(aiMessageService.getMessagesId(sessionId));
    }

    // 保存对话消息
    @PostMapping("/api/ai/saveMessage")
    public Result saveMessage(@RequestBody AiMessageInput aiMessageInput) {
        return Results.success(aiMessageService.saveMessage(aiMessageInput));
    }
}
