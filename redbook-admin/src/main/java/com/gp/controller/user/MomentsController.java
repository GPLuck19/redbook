package com.gp.controller.user;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.gp.annotation.Log;
import com.gp.dto.req.user.MomentCommentsDao;
import com.gp.dto.req.user.MomentDao;
import com.gp.dto.req.user.MomentListDao;
import com.gp.dto.resp.user.MomentCommentVo;
import com.gp.dto.resp.user.MomentVo;
import com.gp.enums.OperatorType;
import com.gp.exception.ServiceException;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TMomentCommentsService;
import com.gp.service.TMomentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MomentsController {

    private final TMomentsService momentsService;

    private final TMomentCommentsService momentCommentsService;

    /**
     * 获取朋友圈信息
     */
    @Log(title = "获取朋友圈信息", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/getUserMoments")
    @PostMapping("/api/user-service/getUserMoments")
    public Result<PageResponse<MomentVo>> getUserMoments(@RequestBody MomentListDao requestParam) {
        return Results.success(momentsService.getUserMoments(requestParam));
    }


    /**
     * 发布朋友圈
     */
    @Log(title = "发布朋友圈", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/publishMoment")
    @PostMapping("/api/user-service/publishMoment")
    public Result publishMoment(@RequestBody MomentDao requestParam) {
        return Results.success(momentsService.publishMoment(requestParam));
    }

    /**
     * 删除朋友圈信息
     */
    @Log(title = "删除朋友圈信息", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/deleteMoment")
    @DeleteMapping("/api/user-service/deleteMoment")
    public Result deleteMoment(@RequestParam("momentId") Long momentId) {
        return Results.success(momentsService.removeMoment(momentId));
    }

    /**
     * 获取朋友圈所有评论
     */
    @GetMapping("/api/user-service/getCommentsBymomentId")
    public Result<List<MomentCommentVo>> getCommentsBymomentId(@RequestParam("momentId") Long momentId) {
        try {
            return Results.success(momentCommentsService.getCommentsBymomentId(momentId));
        } catch (Exception e) {
            throw new ServiceException("获取朋友圈所有评论!");
        }
    }


    /**
     * 添加评论或回复
     */
    @Log(title = "删除朋友圈信息", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/addComment")
    @PostMapping("/api/user-service/addComment")
    public Result addComment(@RequestBody MomentCommentsDao comment) {
        try {
            momentCommentsService.addComment(comment);
        } catch (Exception e) {
            throw new ServiceException("评论失败!");
        }
        return Results.success();
    }

}
