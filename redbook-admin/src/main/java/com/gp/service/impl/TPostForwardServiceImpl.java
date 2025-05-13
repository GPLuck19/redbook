package com.gp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.dto.req.content.ForwardPostDao;
import com.gp.dto.req.content.SendMessageDao;
import com.gp.dto.req.user.UserSendMessageDao;
import com.gp.entity.TPostForward;
import com.gp.mapper.TPostForwardMapper;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TPostForwardService;
import com.gp.service.TUserMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【t_post_forward(文章转发文章)】的数据库操作Service实现
* @createDate 2024-12-20 10:06:03
*/
@Service
@RequiredArgsConstructor
public class TPostForwardServiceImpl extends ServiceImpl<TPostForwardMapper, TPostForward>
    implements TPostForwardService{

    private final TUserMessageService userMessageService;

    @Override
    public Result forwardPost(ForwardPostDao requestParam) {
        requestParam.getReceiverIds().stream().forEach(s->{
            TPostForward tPostForward = TPostForward.builder()
                    .postId(requestParam.getPostId())
                    .receiverId(s)
                    .pushId(requestParam.getPushId())
                    .title(requestParam.getTitle())
                    .build();
            save(tPostForward);
            //调用远程服务，补充发送者信息
            UserSendMessageDao sendMessageDao = UserSendMessageDao.builder()
                    .content(requestParam.getTitle())
                    .userId(requestParam.getPushId())
                    .friendId(s)
                    .originalMessageId(requestParam.getPostId())
                    .messageType(2)
                    .build();
            userMessageService.sendMessage(sendMessageDao);
        });
        return Results.success();
    }
}




