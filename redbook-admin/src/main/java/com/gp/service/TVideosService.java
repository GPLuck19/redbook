package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.dto.req.content.VideoDo;
import com.gp.dto.req.content.VideoListDo;
import com.gp.dto.resp.content.VideoVo;
import com.gp.entity.TVideos;
import com.gp.page.PageResponse;
import com.gp.result.Result;

/**
* @author Administrator
* @description 针对表【t_videos(视频表)】的数据库操作Service
* @createDate 2024-12-23 20:58:29
*/
public interface TVideosService extends IService<TVideos> {

    Result addMedium(VideoDo requestParam);

    PageResponse<VideoVo> findMediumList(VideoListDo requestParam);

    Result deleteMediumById(Long videoId);

    PageResponse<VideoVo> findVideoList(VideoListDo requestParam);

}
