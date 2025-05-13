package com.gp.controller.content;

import com.gp.dto.req.content.MessageListDao;
import com.gp.dto.resp.content.MessageListVo;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final TMessageService messageService;

    /**
     * 获取通知列表信息
     */
    @PostMapping("/api/content-service/findMessageList")
    public Result<PageResponse<MessageListVo>> findMessageList(@RequestBody MessageListDao requestParam) {
        return Results.success(messageService.findMessageList(requestParam));
    }


    /**
     * 删除通知消息
     */
    @PostMapping("/api/content-service/markMessageRead")
    public Result markMessageRead(@RequestParam("messageId") Long messageId) {
        return Results.success(messageService.markMessageRead(messageId));
    }
    
}
