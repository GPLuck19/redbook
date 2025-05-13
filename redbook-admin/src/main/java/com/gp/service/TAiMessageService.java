package com.gp.service;

import com.gp.dto.req.ai.AiMessageInput;
import com.gp.entity.TAiMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.result.Result;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_ai_message(对话消息表)】的数据库操作Service
* @createDate 2025-02-10 13:19:06
*/
public interface TAiMessageService extends IService<TAiMessage> {

    List<TAiMessage> getMessagesId(Long sessionId);

    Result saveMessage(AiMessageInput aiMessageInput);
}
