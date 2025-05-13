package com.gp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.entity.TVideoWatchLog;
import com.gp.mapper.TVideoWatchLogMapper;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TVideoWatchLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【t_video_watch_log(用户观看记录表)】的数据库操作Service实现
* @createDate 2024-12-25 09:08:23
*/
@Service
@RequiredArgsConstructor
public class TVideoWatchLogServiceImpl extends ServiceImpl<TVideoWatchLogMapper, TVideoWatchLog>
    implements TVideoWatchLogService{

    private final TVideoWatchLogMapper videoWatchLogMapper;

    @Override
    public List<Long> getWatchedVideosFromRedis(Long userId) {
        return videoWatchLogMapper.selectList(
                new QueryWrapper<TVideoWatchLog>().eq("user_id", userId)
        ).stream().map(TVideoWatchLog::getVideoId).collect(Collectors.toList());
    }

    @Override
    public Result recordUserWatch(Long userId, Long videoId) {
        TVideoWatchLog videoWatchLog = new TVideoWatchLog();
        videoWatchLog.setUserId(userId);
        videoWatchLog.setVideoId(videoId);
        videoWatchLog.setWatchTime(new Date());
        videoWatchLogMapper.insert(videoWatchLog);
        return Results.success();
    }
}




