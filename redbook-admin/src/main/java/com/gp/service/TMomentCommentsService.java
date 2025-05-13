package com.gp.service;

import com.gp.dto.req.user.MomentCommentsDao;
import com.gp.dto.resp.user.MomentCommentVo;
import com.gp.entity.TMomentComments;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_moment_comments(朋友圈评论表)】的数据库操作Service
* @createDate 2025-01-06 14:07:55
*/
public interface TMomentCommentsService extends IService<TMomentComments> {

    List<MomentCommentVo> getCommentsBymomentId(Long momentId);

    void addComment(MomentCommentsDao comment);

    Boolean deleteMomentCommentsById(Long momentId);

}
