package com.gp.listener;

import com.gp.common.constant.ContentRedisKey;
import com.gp.entity.TVideos;
import com.gp.es.esMapping.VideoMapping;
import com.gp.es.service.VideoInfoRepository;
import com.gp.listener.event.DeleteEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
@Slf4j
public class ContentActionListener {

    private final VideoInfoRepository videoInfoRepository;

    private final RedissonClient redissonClient;


    /**
     * 视频审核补充es库
     */
    @Async
    @EventListener
    public void doPassVideo(TVideos video) {
        VideoMapping videoMapping = new VideoMapping();
        videoMapping.setVideoId(video.getVideoId());
        videoMapping.setTitle(video.getTitle());
        videoMapping.setCoverImage(video.getCoverImage());
        videoMapping.setDuration(video.getDuration());
        videoMapping.setCreateTime(video.getCreateTime());
        videoMapping.setVideoUrl(video.getVideoUrl());
        videoMapping.setUserId(video.getUserId());
        String key = ContentRedisKey.VIDEO_LIKE_KEY + video.getVideoId();
        RScoredSortedSet<Long> likeSet = redissonClient.getScoredSortedSet(key);
        videoMapping.setLikeCount(likeSet.size());
        String collectKey = ContentRedisKey.VIDEO_COLLECTION_KEY + video.getVideoId();
        RScoredSortedSet<Long> collectSet = redissonClient.getScoredSortedSet(collectKey);
        videoMapping.setCollectionCount(collectSet.size());
        videoInfoRepository.save(videoMapping);
        log.info("视频数据插入es成功！");
    }
}
