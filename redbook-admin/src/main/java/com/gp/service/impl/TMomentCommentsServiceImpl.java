package com.gp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.dto.req.user.MomentCommentsDao;
import com.gp.dto.resp.content.CommentVo;
import com.gp.dto.resp.user.MomentCommentVo;
import com.gp.dto.resp.user.UserInfo;
import com.gp.entity.*;
import com.gp.exception.ServiceException;
import com.gp.mapper.TMessageMapper;
import com.gp.mapper.TMomentsMapper;
import com.gp.service.TMomentCommentsService;
import com.gp.mapper.TMomentCommentsMapper;
import com.gp.service.TUserService;
import com.gp.utils.SseMessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【t_moment_comments(朋友圈评论表)】的数据库操作Service实现
* @createDate 2025-01-06 14:07:55
*/
@Service
@RequiredArgsConstructor
public class TMomentCommentsServiceImpl extends ServiceImpl<TMomentCommentsMapper, TMomentComments>
    implements TMomentCommentsService{

    private final TMomentsMapper momentsMapper;

    private final TMessageMapper messageMapper;

    private final TUserService userService;

    @Override
    public List<MomentCommentVo> getCommentsBymomentId(Long momentId) {
        // 获取根评论
        LambdaQueryWrapper<TMomentComments> queryWrapper = Wrappers.lambdaQuery(TMomentComments.class)
                .eq(TMomentComments::getMomentId, momentId)
                .eq(TMomentComments::getParentId, 0)
                .orderByDesc(TMomentComments::getCreateTime);

        List<TMomentComments> rootComments = this.list(queryWrapper);

        // 获取所有根评论的用户信息，批量查询
        Set<Long> userIds = rootComments.stream()
                .map(TMomentComments::getUserId)
                .collect(Collectors.toSet());

        // 获取所有子评论的用户 ID
        Set<Long> replyUserIds = rootComments.stream()
                .flatMap(rootComment -> getReplies(rootComment.getId(), new HashMap<>()).stream())
                .map(MomentCommentVo::getUserId)
                .collect(Collectors.toSet());

        // 合并根评论和子评论的用户 ID
        userIds.addAll(replyUserIds);

        Map<Long, UserInfo> userInfoMap = userService.getUserInfoByIds(userIds);


        // 转换根评论为 CommentVo，并填充用户信息
        List<MomentCommentVo> commentVos = rootComments.stream()
                .map(rootComment -> {
                    // 创建 CommentVo 并填充用户信息
                    MomentCommentVo commentVo = BeanUtil.toBean(rootComment, MomentCommentVo.class);
                    UserInfo userVo = userInfoMap.get(rootComment.getUserId());
                    if (userVo != null) {
                        commentVo.setRealName(userVo.getRealName());
                        commentVo.setRegion(userVo.getRegion());
                    }
                    // 获取子评论并递归处理
                    List<MomentCommentVo> replies = getReplies(rootComment.getId(), userInfoMap);
                    commentVo.setReplies(replies); // 设置子评论
                    return commentVo;
                })
                .collect(Collectors.toList());

        return commentVos;
    }

    private List<MomentCommentVo> getReplies(Long parentId, Map<Long, UserInfo> userInfoMap) {
        // 获取所有子评论
        LambdaQueryWrapper<TMomentComments> wrapper = Wrappers.lambdaQuery(TMomentComments.class)
                .eq(TMomentComments::getParentId, parentId)
                .orderByDesc(TMomentComments::getCreateTime);

        List<TMomentComments> replies = this.list(wrapper);

        // 转换并递归获取子评论的子评论
        return replies.stream()
                .map(reply -> {
                    MomentCommentVo replyVo = BeanUtil.toBean(reply, MomentCommentVo.class);
                    // 获取用户信息并填充
                    UserInfo userVo = userInfoMap.get(reply.getUserId());
                    if (userVo != null) {
                        replyVo.setRealName(userVo.getRealName());
                        replyVo.setRegion(userVo.getRegion());
                    }
                    // 递归获取子评论的子评论
                    List<MomentCommentVo> childReplies = getReplies(reply.getId(), userInfoMap);
                    replyVo.setReplies(childReplies); // 设置子评论
                    return replyVo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void addComment(MomentCommentsDao comment) {
        try {
            TMomentComments comments = TMomentComments.builder().parentId(comment.getParentId()).userId(comment.getUserId()).content(comment.getContent()).momentId(comment.getMomentId()).build();
            TMoments moments = momentsMapper.selectById(comment.getMomentId());
            //存储消息列表
            TMessage message = TMessage.builder().status(0).pushId(comment.getUserId()).receiverId(moments.getUserId())
                    .message("用户: "+comment.getUserId()+"刚刚评论了您的朋友圈")
                    .type(1)
                    .objectId(comment.getMomentId())
                    .build();
            messageMapper.insert(message);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("receiverId",moments.getUserId());
            jsonObject.put("message",message.getMessage());
            jsonObject.put("objectId",comment.getMomentId());
            jsonObject.put("messageType","COMMENT");
            SseMessageUtils.sendMessage(moments.getUserId(),jsonObject.toJSONString());
            this.save(comments);
        } catch (Exception e) {
            throw new ServiceException("评论错误!");
        }
    }

    @Override
    public Boolean deleteMomentCommentsById(Long momentId) {
        Wrapper<TMomentComments> wrapper = Wrappers.lambdaQuery(TMomentComments.class)
                .eq(TMomentComments::getMomentId, momentId);
        return this.remove(wrapper);
    }
}




