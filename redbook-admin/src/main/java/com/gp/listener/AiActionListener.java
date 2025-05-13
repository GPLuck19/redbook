package com.gp.listener;

import com.gp.dto.req.ai.AiMessageInput;
import com.gp.entity.TAiMessage;
import com.gp.service.TAiMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;


@RequiredArgsConstructor
@Component
@Slf4j
public class AiActionListener {

    private final TAiMessageService messageService;


    /**
     * 视频审核补充es库
     */
    @Async
    @EventListener
    public void doSaveMessage(AiMessageInput aiMessageInput) {
        TAiMessage aiMessage = TAiMessage.builder()
                .type(aiMessageInput.getType())
                .textContent(aiMessageInput.getTextContent())
                .medias(aiMessageInput.getMedias().toString())
                .aiSessionId(aiMessageInput.getSessionId())
                .creatorId(aiMessageInput.getUserId())
                .createdTime(new Date())
                .build();
        messageService.save(aiMessage);
        log.info("用户:"+aiMessage.getCreatorId()+"  ai问答数据插入成功！");
    }
}
