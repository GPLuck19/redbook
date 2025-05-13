package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.dto.req.content.AddCommentsDao;
import com.gp.dto.resp.content.CommentVo;
import com.gp.entity.TComments;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_comments(评论表)】的数据库操作Service
* @createDate 2024-11-21 09:49:51
*/
public interface TCommentsService extends IService<TComments> {

    Boolean deleteCommentsById(Long postId);

    // 获取某文章的所有评论（包含子评论）
    List<CommentVo> getCommentsByPostId(Long postId);

    void addComment(AddCommentsDao comment);
}
