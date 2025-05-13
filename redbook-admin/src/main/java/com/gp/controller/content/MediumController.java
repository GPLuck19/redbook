package com.gp.controller.content;


import com.gp.dto.req.content.VideoDo;
import com.gp.dto.req.content.VideoListDo;
import com.gp.dto.resp.content.VideoVo;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TVideoWatchLogService;
import com.gp.service.TVideosService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 视频媒资控制层
 */
@RestController
@RequiredArgsConstructor
public class MediumController {

    private final TVideosService videosService;

    private final TVideoWatchLogService videoWatchLogService;


    /**
     * 新增媒体
     */
    @PostMapping("/api/content-service/addMedium")
    public Result addPost(@RequestBody VideoDo requestParam) {
        return Results.success(videosService.addMedium(requestParam));
    }


    /**
     * 根据id获取媒体
     */
    @GetMapping("/api/content-service/getMediumById")
    public Result getMediumById(@RequestParam("videoId") Long videoId) {
        return Results.success(videosService.getById(videoId));
    }


    /**
     * 删除媒体
     */
    @DeleteMapping("/api/content-service/deleteMediumById")
    public Result deleteMediumById(@RequestParam("videoId") Long videoId) {
        return Results.success(videosService.deleteMediumById(videoId));
    }


    /**
     * 我的-》获取媒体列表信息
     */
    @PostMapping("/api/content-service/findMediumList")
    public Result<PageResponse<VideoVo>> findMediumList(@RequestBody VideoListDo requestParam) {
        return Results.success(videosService.findMediumList(requestParam));
    }

    /**
     * 视频流媒体-》获取文章列表信息
     */
    @PostMapping("/api/content-service/findVideoList")
    public Result<PageResponse<VideoVo>> findVideoList(@RequestBody VideoListDo requestParam) {
        return Results.success(videosService.findVideoList(requestParam));
    }


    /**
     * 插入媒体观看记录信息
     */
    @PostMapping("/api/content-service/recordUserWatch")
    public Result recordUserWatch(@RequestParam("userId") Long userId,@RequestParam("videoId") Long videoId) {
        return Results.success(videoWatchLogService.recordUserWatch(userId,videoId));
    }

}
