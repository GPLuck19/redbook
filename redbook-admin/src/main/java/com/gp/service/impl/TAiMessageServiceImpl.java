package com.gp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.dto.req.ai.AiMessageInput;
import com.gp.entity.TAiMessage;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TAiMessageService;
import com.gp.mapper.TAiMessageMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author Administrator
* @description 针对表【t_ai_message(对话消息表)】的数据库操作Service实现
* @createDate 2025-02-10 13:19:06
*/
@Service
public class TAiMessageServiceImpl extends ServiceImpl<TAiMessageMapper, TAiMessage>
    implements TAiMessageService{

    @Override
    public List<TAiMessage> getMessagesId(Long sessionId) {
        LambdaQueryWrapper<TAiMessage> wrapper = Wrappers.lambdaQuery(TAiMessage.class).eq(TAiMessage::getAiSessionId, sessionId)
                .orderByAsc(TAiMessage::getCreatedTime);
        return this.list(wrapper);
    }

    @Override
    public Result saveMessage(AiMessageInput aiMessageInput) {
        TAiMessage aiMessage = TAiMessage.builder()
                .type(aiMessageInput.getType())
                .textContent(aiMessageInput.getTextContent())
                .medias(aiMessageInput.getMedias().toString())
                .aiSessionId(aiMessageInput.getSessionId())
                .creatorId(aiMessageInput.getUserId())
                .createdTime(new Date())
                .build();
        this.save(aiMessage);
        return Results.success();
    }

}




