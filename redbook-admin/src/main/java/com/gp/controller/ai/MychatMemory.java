package com.gp.controller.ai;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gp.dto.req.ai.AiMessageInput;
import com.gp.dto.req.ai.Medias;
import com.gp.entity.TAiMessage;
import com.gp.exception.ServiceException;
import com.gp.service.TAiMessageService;
import com.gp.service.TAiSessionService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.model.Media;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class MychatMemory implements ChatMemory {

    private final TAiSessionService aiSessionService;

    private final TAiMessageService aiMessageService;

    /**
     * 不实现，手动前端发起请求保存用户的消息和大模型回复的消息
     */
    @Override
    public void add(String conversationId, List<Message> messages) {

    }

    /**
     * 查询会话内的消息最新n条历史记录
     *
     * @param conversationId 会话id
     * @param lastN          最近n条
     * @return org.springframework.ai.chat.messages.Message格式的消息
     */
    @Override
    public List<Message> get(String conversationId, int lastN) {
        LambdaQueryWrapper<TAiMessage> last = Wrappers.lambdaQuery(TAiMessage.class).eq(TAiMessage::getAiSessionId, conversationId).orderByDesc(TAiMessage::getCreatedTime)
                .last("LIMIT  " + lastN);
        List<TAiMessage> list = aiMessageService.list(last);
        List<AiMessageInput> aiMessages = BeanUtil.copyToList(list, AiMessageInput.class);
        return aiMessages
                .stream()
                // 转成Message对象
                .map(MychatMemory::toSpringAiMessage)
                .toList();
    }

    /**
     * 清除会话内的消息
     *
     * @param conversationId 会话id
     */
    @Override
    public void clear(String conversationId) {
        aiSessionService.deleteConversation(Long.valueOf(conversationId));
    }


    public static Message toSpringAiMessage(AiMessageInput aiMessage) {
        List<Media> mediaList = new ArrayList<>();
        if (!CollectionUtil.isEmpty(aiMessage.getMedias())) {
            mediaList = aiMessage.getMedias().stream().map(MychatMemory::toSpringAiMedia).toList();
        }
        if (aiMessage.getType().equals(MessageType.ASSISTANT.getValue())) {
            return new AssistantMessage(aiMessage.getTextContent());
        }
        if (aiMessage.getType().equals(MessageType.USER.getValue())) {
            return new UserMessage(aiMessage.getTextContent(), mediaList);
        }
        if (aiMessage.getType().equals(MessageType.SYSTEM.getValue())) {
            return new SystemMessage(aiMessage.getTextContent());
        }
        throw new ServiceException("不支持的消息类型");
    }

    @SneakyThrows
    public static Media toSpringAiMedia(Medias media) {
        return new Media(new MediaType(media.getType()), new URL(media.getData()));
    }
}