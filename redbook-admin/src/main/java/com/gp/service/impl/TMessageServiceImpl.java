package com.gp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.dto.req.content.MessageListDao;
import com.gp.dto.resp.content.MessageListVo;
import com.gp.entity.TMessage;
import com.gp.mapper.TMessageMapper;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TMessageService;
import com.gp.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
* @author Administrator
* @description 针对表【t_message(消息通知表)】的数据库操作Service实现
* @createDate 2024-11-27 16:07:33
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class TMessageServiceImpl extends ServiceImpl<TMessageMapper, TMessage>
    implements TMessageService{


    private final TMessageMapper messageMapper;




    @Override
    public PageResponse<MessageListVo> findMessageList(MessageListDao requestParam) {
        LambdaQueryWrapper<TMessage> queryWrapper = Wrappers.lambdaQuery(TMessage.class)
                .eq(TMessage::getStatus,0)
                .orderByDesc(TMessage::getCreateTime);
        if(ObjectUtil.isNotEmpty(requestParam.getUserId())){
            queryWrapper.eq(TMessage::getReceiverId, requestParam.getUserId());
        }
        IPage<TMessage> messagesPage = messageMapper.selectPage(PageUtil.convert(requestParam), queryWrapper);
        return PageUtil.convert(messagesPage, each -> {
            MessageListVo actualResult = BeanUtil.toBean(each, MessageListVo.class);
            return actualResult;
        });
    }

    @Override
    public Result markMessageRead(Long messageId) {
        TMessage message = messageMapper.selectById(messageId);
        message.setStatus(1);
        messageMapper.updateById(message);
        return Results.success();
    }
}




