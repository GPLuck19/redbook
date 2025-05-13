package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.dto.req.content.MessageListDao;
import com.gp.dto.resp.content.MessageListVo;
import com.gp.entity.TMessage;
import com.gp.page.PageResponse;
import com.gp.result.Result;

/**
* @author Administrator
* @description 针对表【t_message(消息通知表)】的数据库操作Service
* @createDate 2024-11-27 16:07:33
*/
public interface TMessageService extends IService<TMessage> {

    PageResponse<MessageListVo> findMessageList(MessageListDao requestParam);

    Result markMessageRead(Long messageId);
}
