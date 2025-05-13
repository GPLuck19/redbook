package com.gp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.dto.req.ai.AiSession;
import com.gp.entity.TAiMessage;
import com.gp.entity.TAiSession;
import com.gp.mapper.TAiMessageMapper;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TAiSessionService;
import com.gp.mapper.TAiSessionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
* @author guidada
* @description 针对表【t_ai_session(对话表)】的数据库操作Service实现
* @createDate 2025-02-08 09:43:53
*/
@Service
@RequiredArgsConstructor
public class TAiSessionServiceImpl extends ServiceImpl<TAiSessionMapper, TAiSession>
    implements TAiSessionService{

    private final TAiMessageMapper aiMessageMapper;

    @Override
    public Long createConversation(AiSession session) {
        TAiSession aiSession = TAiSession.builder().userId(session.getUserId()).name(session.getName()).createdTime(new Date()).build();
        this.save(aiSession);
        return aiSession.getSessionId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteConversation(Long sessionId) {
        this.removeById(sessionId);
        //刪除关联信息
        LambdaQueryWrapper<TAiMessage> wrapper = Wrappers.lambdaQuery(TAiMessage.class).eq(TAiMessage::getAiSessionId, sessionId);
        aiMessageMapper.delete(wrapper);
        return Results.success();
    }

    @Override
    public List<TAiSession> getConversationById(Long userId) {
        LambdaQueryWrapper<TAiSession> wrapper = Wrappers.lambdaQuery(TAiSession.class).eq(TAiSession::getUserId, userId).orderByDesc(TAiSession::getCreatedTime);
        return this.list(wrapper);
    }
}




