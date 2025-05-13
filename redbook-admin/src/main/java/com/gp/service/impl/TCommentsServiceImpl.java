package com.gp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.dto.req.content.AddCommentsDao;
import com.gp.dto.resp.content.CommentVo;
import com.gp.dto.resp.user.UserInfo;
import com.gp.entity.TComments;
import com.gp.entity.TMessage;
import com.gp.entity.TPosts;
import com.gp.exception.ServiceException;
import com.gp.mapper.TCommentsMapper;
import com.gp.mapper.TMessageMapper;
import com.gp.mapper.TPostsMapper;
import com.gp.service.TCommentsService;
import com.gp.service.TUserService;
import com.gp.utils.SseMessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【t_comments(评论表)】的数据库操作Service实现
* @createDate 2024-11-21 09:49:51
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class TCommentsServiceImpl extends ServiceImpl<TCommentsMapper, TComments>
    implements TCommentsService{


    private final TPostsMapper postsMapper;

    private final TMessageMapper messageMapper;

    private final TUserService userService;




    @Override
    public Boolean deleteCommentsById(Long postId) {
        LambdaQueryWrapper<TComments> wrapper = Wrappers.lambdaQuery(TComments.class).eq(TComments::getPostId, postId);
        return this.remove(wrapper);
    }

    @Override
    public List<CommentVo> getCommentsByPostId(Long postId) {
        // 获取根评论
        LambdaQueryWrapper<TComments> queryWrapper = Wrappers.lambdaQuery(TComments.class)
                .eq(TComments::getPostId, postId)
                .eq(TComments::getParentId, 0)
                .orderByDesc(TComments::getCreateTime);

        List<TComments> rootComments = this.list(queryWrapper);

        // 获取所有根评论的用户信息，批量查询
        Set<Long> userIds = rootComments.stream()
                .map(TComments::getUserId)
                .collect(Collectors.toSet());

        // 获取所有子评论的用户 ID
        Set<Long> replyUserIds = rootComments.stream()
                .flatMap(rootComment -> getReplies(rootComment.getCommentId(), new HashMap<>()).stream())
                .map(CommentVo::getUserId)
                .collect(Collectors.toSet());

        // 合并根评论和子评论的用户 ID
        userIds.addAll(replyUserIds);

        Map<Long, UserInfo> userInfoMap = userService.getUserInfoByIds(userIds);


        // 转换根评论为 CommentVo，并填充用户信息
        List<CommentVo> commentVos = rootComments.stream()
                .map(rootComment -> {
                    // 创建 CommentVo 并填充用户信息
                    CommentVo commentVo = BeanUtil.toBean(rootComment, CommentVo.class);
                    UserInfo userVo = userInfoMap.get(rootComment.getUserId());
                    if (userVo != null) {
                        commentVo.setRealName(userVo.getRealName());
                        commentVo.setRegion(userVo.getRegion());
                    }
                    // 获取子评论并递归处理
                    List<CommentVo> replies = getReplies(rootComment.getCommentId(), userInfoMap);
                    commentVo.setReplies(replies); // 设置子评论
                    return commentVo;
                })
                .collect(Collectors.toList());

        return commentVos;
    }


    private List<CommentVo> getReplies(Long parentId, Map<Long, UserInfo> userInfoMap) {
        // 获取所有子评论
        LambdaQueryWrapper<TComments> wrapper = Wrappers.lambdaQuery(TComments.class)
                .eq(TComments::getParentId, parentId)
                .orderByDesc(TComments::getCreateTime);

        List<TComments> replies = this.list(wrapper);

        // 转换并递归获取子评论的子评论
        return replies.stream()
                .map(reply -> {
                    CommentVo replyVo = BeanUtil.toBean(reply, CommentVo.class);
                    // 获取用户信息并填充
                    UserInfo userVo = userInfoMap.get(reply.getUserId());
                    if (userVo != null) {
                        replyVo.setRealName(userVo.getRealName());
                        replyVo.setRegion(userVo.getRegion());
                    }
                    // 递归获取子评论的子评论
                    List<CommentVo> childReplies = getReplies(reply.getCommentId(), userInfoMap);
                    replyVo.setReplies(childReplies); // 设置子评论
                    return replyVo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addComment(AddCommentsDao comment) {
        try {
            TComments comments = TComments.builder().parentId(comment.getParentId()).userId(comment.getUserId()).content(comment.getContent()).postId(comment.getPostId()).build();
            Long postId = comment.getPostId();
            TPosts posts = postsMapper.selectById(postId);
            Long receiverId = posts.getUserId();
            //存储消息列表
            TMessage message = TMessage.builder().status(0).pushId(comment.getUserId()).receiverId(receiverId)
                    .message("用户: "+posts.getUsername()+"刚刚评论了您的文章"+posts.getTitle()+":"+comment.getContent())
                    .type(1)
                    .objectId(postId)
                    .build();
            messageMapper.insert(message);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("receiverId",receiverId);
            jsonObject.put("message",message.getMessage());
            jsonObject.put("objectId",postId);
            jsonObject.put("messageType","COMMENT");
            SseMessageUtils.sendMessage(receiverId,jsonObject.toJSONString());
            this.save(comments);
        } catch (Exception e) {
            throw new ServiceException("评论错误!");
        }

    }

}




