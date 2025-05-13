package com.gp.controller.content;


import cn.dev33.satoken.annotation.SaCheckDisable;
import com.gp.dto.req.content.AddCommentsDao;
import com.gp.dto.resp.content.CommentVo;
import com.gp.exception.ServiceException;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TCommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentsController {

    private final TCommentsService commentsService;

    /**
     * 获取某文章的所有评论（包含子评论）
     */
    @GetMapping("/api/content-service/getCommentsByPostId")
    public Result<List<CommentVo>> getCommentsByPostId(@RequestParam("postId") Long postId) {
        try {
            return Results.success(commentsService.getCommentsByPostId(postId));
        } catch (Exception e) {
            throw new ServiceException("获取某文章的所有评论!");
        }
    }


    /**
     * 添加评论或回复
     */
    @SaCheckDisable("addComment")
    @PostMapping("/api/content-service/addComment")
    public Result addComment(@RequestBody AddCommentsDao comment) {
        try {
            commentsService.addComment(comment);
        } catch (Exception e) {
            throw new ServiceException("评论失败!");
        }
        return Results.success();
    }

}
