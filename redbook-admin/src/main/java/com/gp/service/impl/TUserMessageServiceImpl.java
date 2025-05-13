package com.gp.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.dto.req.user.FriendMessageDao;
import com.gp.dto.req.user.UserSendMessageDao;
import com.gp.dto.resp.user.FriendMessageListVo;
import com.gp.entity.TUserMessage;
import com.gp.exception.ServiceException;
import com.gp.mapper.TUserMessageMapper;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TUserFriendService;
import com.gp.service.TUserMessageService;
import com.gp.utils.PageUtil;
import com.gp.utils.WebSocketUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author Administrator
* @description 针对表【t_user_message(用户私信表)】的数据库操作Service实现
* @createDate 2024-11-29 10:19:42
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class TUserMessageServiceImpl extends ServiceImpl<TUserMessageMapper, TUserMessage>
    implements TUserMessageService{

    private final TUserMessageMapper userMessageMapper;

    private final TUserFriendService userFriendService;





    @SneakyThrows
    @Override
    public Result sendMessage(UserSendMessageDao requestParam) {
        // 检查好友关系是否有效
        Boolean aBoolean = userFriendService.isFriend(requestParam.getUserId(), requestParam.getFriendId());
        if (!aBoolean) {
            throw new RuntimeException("不是好友，无法发送私信");
        }
        // 保存消息
        TUserMessage message = new TUserMessage();
        message.setSenderId(requestParam.getUserId());
        message.setReceiverId(requestParam.getFriendId());
        message.setContent(requestParam.getContent());
        message.setMessageType(requestParam.getMessageType());
        message.setOriginalMessageId(requestParam.getOriginalMessageId());
        message.setStatus(0); // 未读状态
        userMessageMapper.insert(message);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messageId", message.getId());
        jsonObject.put("senderId", requestParam.getUserId());
        jsonObject.put("receiverId", requestParam.getFriendId());
        jsonObject.put("content", requestParam.getContent());
        jsonObject.put("createTime", message.getCreateTime());
        jsonObject.put("messageType", requestParam.getMessageType());
        jsonObject.put("originalMessageId", requestParam.getOriginalMessageId());
        WebSocketUtils.sendMessage(requestParam.getFriendId(),jsonObject.toJSONString());
        return Results.success(message.getId());
    }

    @Override
    public PageResponse<TUserMessage> getChatHistory(FriendMessageDao requestParam) {
        QueryWrapper<TUserMessage> wrapper = new QueryWrapper<>();
        wrapper.and(w -> w.eq("sender_id", requestParam.getUserId()).eq("receiver_id", requestParam.getFriendId()))
                .or(w -> w.eq("sender_id", requestParam.getFriendId()).eq("receiver_id", requestParam.getUserId()));
        wrapper.orderByAsc("create_time");
        IPage<TUserMessage> messagePage = userMessageMapper.selectPage(PageUtil.convert(requestParam), wrapper);
        return PageResponse.<TUserMessage>builder()
                .current(messagePage.getCurrent())
                .size(messagePage.getSize())
                .records(messagePage.getRecords())
                .total(messagePage.getTotal())
                .build();
    }

    @Override
    public List<FriendMessageListVo> getFriendMessageList(Long userId) {
        return userMessageMapper.FriendMessageList(userId);
    }

    @Override
    public Result updateMessageStatus(Long messageId, Integer status) {
        TUserMessage message = userMessageMapper.selectById(messageId);
        if (message != null) {
            message.setStatus(status);
            message.setUpdateTime(new Date());
            userMessageMapper.updateById(message);
            return Results.success();
        }
        throw new ServiceException("更新短信失败!");
    }

}




