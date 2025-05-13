package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.dto.req.user.FriendMessageDao;
import com.gp.dto.req.user.UserSendMessageDao;
import com.gp.dto.resp.user.FriendMessageListVo;
import com.gp.entity.TUserMessage;
import com.gp.page.PageResponse;
import com.gp.result.Result;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_user_message(用户私信表)】的数据库操作Service
* @createDate 2024-11-29 10:19:42
*/
public interface TUserMessageService extends IService<TUserMessage> {


    Result sendMessage(UserSendMessageDao requestParam);

    PageResponse<TUserMessage> getChatHistory(FriendMessageDao requestParam);

    List<FriendMessageListVo> getFriendMessageList(Long userId);

    Result updateMessageStatus(Long messageId, Integer status);
}
