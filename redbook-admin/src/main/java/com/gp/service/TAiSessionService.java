package com.gp.service;

import com.gp.dto.req.ai.AiSession;
import com.gp.entity.TAiSession;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.result.Result;

import java.util.List;

/**
* @author guidada
* @description 针对表【t_ai_session(对话表)】的数据库操作Service
* @createDate 2025-02-08 09:43:53
*/
public interface TAiSessionService extends IService<TAiSession> {

    Long createConversation(AiSession aiSession);

    Result deleteConversation(Long sessionId);

    List<TAiSession> getConversationById(Long userId);
}
