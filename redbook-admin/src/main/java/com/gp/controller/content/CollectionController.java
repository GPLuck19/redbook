package com.gp.controller.content;

import com.gp.common.constant.ContentRedisKey;
import com.gp.exception.ServiceException;
import com.gp.result.Result;
import com.gp.result.Results;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

import static com.gp.constant.TaskConstant.POST_COLLECT_EVENT_KEY;
import static com.gp.constant.TaskConstant.VIDEO_COLLECT_EVENT_KEY;


/**
 * 用户收藏控制层
 */
@RestController
@RequiredArgsConstructor
public class CollectionController {


    private final RedissonClient redissonClient;


    /**
     * 查看文章是否收藏
     */
    @GetMapping("/api/content-service/iFCollection")
    public Result iFCollection(@RequestParam("postId") Long postId,@RequestParam("userId") Long userId) {
        try {
            String key = ContentRedisKey.POST_COLLECTION_KEY + postId;
            RScoredSortedSet<Long> likeSet = redissonClient.getScoredSortedSet(key);
            return Results.success(likeSet.contains(userId));
        } catch (Exception e) {
            throw new ServiceException("收藏失败!");
        }
    }


    /**
     * 对文章收藏操作
     */
    @PostMapping("/api/content-service/addCollection")
    public Result addCollection(@RequestParam("postId") Long postId,@RequestParam("userId") Long userId) {
        try {
            String key = ContentRedisKey.POST_COLLECTION_KEY + postId;
            RScoredSortedSet<Long> likeSet = redissonClient.getScoredSortedSet(key);
            // 使用当前时间戳作为评分，记录用户ID
            likeSet.add(Instant.now().getEpochSecond(), userId);
            // 增量记录
            String eventData = postId + ":" + userId+ ":1";
            redissonClient.getList(POST_COLLECT_EVENT_KEY).add(eventData);
        } catch (Exception e) {
            throw new ServiceException("点赞失败!");
        }
        return Results.success();
    }


    /**
     * 对文章取消收藏操作
     */
    @PostMapping("/api/content-service/removeCollection")
    public Result removeCollection(@RequestParam("postId") Long postId,@RequestParam("userId") Long userId) {
        try {
            String key = ContentRedisKey.POST_COLLECTION_KEY + postId;
            RScoredSortedSet<Long> likeSet = redissonClient.getScoredSortedSet(key);
            // 使用当前时间戳作为评分，记录用户ID
            likeSet.remove(userId);
            // 减量记录
            String eventData = postId + ":" + userId+ ":2";
            redissonClient.getList(POST_COLLECT_EVENT_KEY).add(eventData);
        } catch (Exception e) {
            throw new ServiceException("取消点赞失败!");
        }
        return Results.success();
    }


    /**
     * 查看视频是否收藏
     */
    @GetMapping("/api/content-service/iFVideoCollection")
    public Result iFVideoCollection(@RequestParam("videoId") Long videoId,@RequestParam("userId") Long userId) {
        try {
            String key = ContentRedisKey.VIDEO_COLLECTION_KEY + videoId;
            RScoredSortedSet<Long> likeSet = redissonClient.getScoredSortedSet(key);
            return Results.success(likeSet.contains(userId));
        } catch (Exception e) {
            throw new ServiceException("收藏失败!");
        }
    }


    /**
     * 对视频收藏操作
     */
    @PostMapping("/api/content-service/addVideoCollection")
    public Result addVideoCollection(@RequestParam("videoId") Long videoId,@RequestParam("userId") Long userId) {
        try {
            String key = ContentRedisKey.VIDEO_COLLECTION_KEY + videoId;
            RScoredSortedSet<Long> likeSet = redissonClient.getScoredSortedSet(key);
            // 使用当前时间戳作为评分，记录用户ID
            likeSet.add(Instant.now().getEpochSecond(), userId);
            // 增量记录
            String eventData = videoId + ":" + userId+ ":1";
            redissonClient.getList(VIDEO_COLLECT_EVENT_KEY).add(eventData);
        } catch (Exception e) {
            throw new ServiceException("点赞失败!");
        }
        return Results.success();
    }


    /**
     * 对视频取消收藏操作
     */
    @PostMapping("/api/content-service/removeVideoCollection")
    public Result removeVideoCollection(@RequestParam("videoId") Long videoId,@RequestParam("userId") Long userId) {
        try {
            String key = ContentRedisKey.VIDEO_COLLECTION_KEY + videoId;
            RScoredSortedSet<Long> likeSet = redissonClient.getScoredSortedSet(key);
            // 使用当前时间戳作为评分，记录用户ID
            likeSet.remove(userId);
            // 减量记录
            String eventData = videoId + ":" + userId+ ":2";
            redissonClient.getList(VIDEO_COLLECT_EVENT_KEY).add(eventData);
        } catch (Exception e) {
            throw new ServiceException("取消点赞失败!");
        }
        return Results.success();
    }
}
