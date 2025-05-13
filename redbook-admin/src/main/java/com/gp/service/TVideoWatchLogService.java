package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.entity.TVideoWatchLog;
import com.gp.result.Result;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_video_watch_log(用户观看记录表)】的数据库操作Service
* @createDate 2024-12-25 09:08:23
*/
public interface TVideoWatchLogService extends IService<TVideoWatchLog> {

    List<Long> getWatchedVideosFromRedis(Long userId);

    Result recordUserWatch(Long userId, Long videoId);

}
