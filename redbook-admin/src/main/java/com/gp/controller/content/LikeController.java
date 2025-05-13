package com.gp.controller.content;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gp.common.constant.ContentRedisKey;
import com.gp.entity.*;
import com.gp.exception.ServiceException;
import com.gp.mapper.*;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.utils.SseMessageUtils;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;

import static com.gp.constant.TaskConstant.POST_LIKE_EVENT_KEY;
import static com.gp.constant.TaskConstant.VIDEO_LIKE_EVENT_KEY;

/**
 * 用户点赞控制层
 */
@RestController
@RequiredArgsConstructor
public class LikeController {

    private final RedissonClient redissonClient;

    private final TPostsMapper postsMapper;

    private final TVideosMapper videosMapper;

    private final TMessageMapper messageMapper;

    private final TLikesMapper likesMapper;

    private final TMomentsMapper momentsMapper;


    /**
     * 查看文章是否点赞
     */
    @GetMapping("/api/content-service/iFLike")
    public Result iFLike(@RequestParam("postId") Long postId,@RequestParam("userId") Long userId) {
        try {
            String key = ContentRedisKey.POST_LIKE_KEY + postId;
            RScoredSortedSet<Long> likeSet = redissonClient.getScoredSortedSet(key);
            return Results.success(likeSet.contains(userId));
        } catch (Exception e) {
            throw new ServiceException("点赞失败!");
        }
    }


    /**
     * 查看视频是否点赞
     */
    @GetMapping("/api/content-service/iFVideoLike")
    public Result iFVideoLike(@RequestParam("videoId") Long videoId,@RequestParam("userId") Long userId) {
        try {
            String key = ContentRedisKey.VIDEO_LIKE_KEY + videoId;
            RScoredSortedSet<Long> likeSet = redissonClient.getScoredSortedSet(key);
            return Results.success(likeSet.contains(userId));
        } catch (Exception e) {
            throw new ServiceException("点赞失败!");
        }
    }


    /**
     * 对视频点赞操作
     */
    @PostMapping("/api/content-service/addVideoLike")
    public Result addVideoLike(@RequestParam("videoId") Long videoId,@RequestParam("userId") Long userId) {
        try {
            String key = ContentRedisKey.VIDEO_LIKE_KEY + videoId;
            RScoredSortedSet<Long> likeSet = redissonClient.getScoredSortedSet(key);
            // 使用当前时间戳作为评分，记录用户ID
            likeSet.add(Instant.now().getEpochSecond(), userId);
            JSONObject jsonObject = new JSONObject();
            TVideos videos = videosMapper.selectById(videoId);
            //存储消息列表
            TMessage message = TMessage.builder().status(0).pushId(userId).receiverId(videos.getUserId())
                    .message("您的视频"+videos.getTitle()+"刚刚收获一份新的点赞哦!快去看看吧").type(2).objectId(videoId).build();
            messageMapper.insert(message);
            jsonObject.put("receiverId",videos.getUserId());
            jsonObject.put("message",message.getMessage());
            jsonObject.put("messageType","LIKE");
            jsonObject.put("objectId",videoId);
            SseMessageUtils.sendMessage(videos.getUserId(), jsonObject.toJSONString());
            // 增量记录
            String eventData = videoId + ":" + userId+ ":1";
            redissonClient.getList(VIDEO_LIKE_EVENT_KEY).add(eventData);
        } catch (Exception e) {
            throw new ServiceException("点赞失败!");
        }
        return Results.success();
    }



    /**
     * 对文章点赞操作
     */
    @PostMapping("/api/content-service/addLike")
    public Result addLike(@RequestParam("postId") Long postId,@RequestParam("userId") Long userId) {
        try {
            String key = ContentRedisKey.POST_LIKE_KEY + postId;
            RScoredSortedSet<Long> likeSet = redissonClient.getScoredSortedSet(key);
            // 使用当前时间戳作为评分，记录用户ID
            likeSet.add(Instant.now().getEpochSecond(), userId);
            JSONObject jsonObject = new JSONObject();
            TPosts posts = postsMapper.selectById(postId);
            //存储消息列表
            TMessage message = TMessage.builder().status(0).pushId(userId).receiverId(posts.getUserId())
                    .message("您的文章"+posts.getTitle()+"刚刚收获一份新的点赞哦!快去看看吧").type(2).objectId(postId).build();
            messageMapper.insert(message);
            jsonObject.put("receiverId",posts.getUserId());
            jsonObject.put("message",message.getMessage());
            jsonObject.put("messageType","LIKE");
            jsonObject.put("objectId",postId);
            SseMessageUtils.sendMessage(posts.getUserId(), jsonObject.toJSONString());
            // 增量记录
            String eventData = postId + ":" + userId+ ":1";
            redissonClient.getList(POST_LIKE_EVENT_KEY).add(eventData);
        } catch (Exception e) {
            throw new ServiceException("点赞失败!");
        }
        return Results.success();
    }

    /**
     * 对朋友圈点赞操作
     */
    @PostMapping("/api/content-service/addMomentLike")
    public Result addMomentLike(@RequestParam("postId") Long momentId,@RequestParam("userId") Long userId) {
        try {
            TLikes likes = TLikes.builder().likeType(3).objId(momentId).userId(userId).build();
            likesMapper.insert(likes);
            TMoments moments = momentsMapper.selectById(momentId);
            //存储消息列表
            TMessage message = TMessage.builder().status(0).pushId(userId).receiverId(moments.getUserId())
                    .message("您的朋友圈刚刚收获一份新的点赞哦!快去看看吧").type(2).objectId(momentId).build();
            messageMapper.insert(message);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("receiverId",moments.getUserId());
            jsonObject.put("message",message.getMessage());
            jsonObject.put("messageType","LIKE");
            jsonObject.put("objectId",momentId);
            SseMessageUtils.sendMessage(moments.getUserId(), jsonObject.toJSONString());
        } catch (Exception e) {
            throw new ServiceException("点赞失败!");
        }
        return Results.success();
    }


    /**
     * 对文章取消点赞操作
     */
    @PostMapping("/api/content-service/removeLike")
    public Result removeLike(@RequestParam("postId") Long postId,@RequestParam("userId") Long userId) {
        try {
            String key = ContentRedisKey.POST_LIKE_KEY + postId;
            RScoredSortedSet<Long> likeSet = redissonClient.getScoredSortedSet(key);
            // 使用当前时间戳作为评分，记录用户ID
            likeSet.remove(userId);
            // 减量记录
            String eventData = postId + ":" + userId+ ":2";
            redissonClient.getList(POST_LIKE_EVENT_KEY).add(eventData);
        } catch (Exception e) {
            throw new ServiceException("取消点赞失败!");
        }
        return Results.success();
    }

    /**
     * 对视频取消点赞操作
     */
    @PostMapping("/api/content-service/removeVideoLike")
    public Result removeVideoLike(@RequestParam("videoId") Long videoId,@RequestParam("userId") Long userId) {
        try {
            String key = ContentRedisKey.VIDEO_LIKE_KEY + videoId;
            RScoredSortedSet<Long> likeSet = redissonClient.getScoredSortedSet(key);
            // 使用当前时间戳作为评分，记录用户ID
            likeSet.remove(userId);
            // 减量记录
            String eventData = videoId + ":" + userId+ ":2";
            redissonClient.getList(VIDEO_LIKE_EVENT_KEY).add(eventData);
        } catch (Exception e) {
            throw new ServiceException("取消点赞失败!");
        }
        return Results.success();
    }

    /**
     * 对朋友圈取消点赞操作
     */
    @PostMapping("/api/content-service/removeMomentLike")
    public Result removeMomentLike(@RequestParam("videoId") Long momentId,@RequestParam("userId") Long userId) {
        LambdaQueryWrapper<TLikes> wrapper = Wrappers.lambdaQuery(TLikes.class).eq(TLikes::getObjId, momentId).eq(TLikes::getLikeType, 3).eq(TLikes::getUserId, userId);
        likesMapper.delete(wrapper);
        return Results.success();
    }



    /**
     * 获取点赞用户ID列表（按时间顺序）
     */
    @GetMapping("/api/content-service/giveLikeList")
    public Result<Set<Long>> giveLikeList(@RequestParam("postId") Long postId) {
        try {
            String key = ContentRedisKey.POST_LIKE_KEY + postId;
            RScoredSortedSet<Long> likeSet = redissonClient.getScoredSortedSet(key);
            // 按时间顺序返回用户ID列表
            return Results.success((Set<Long>) likeSet.readAll());
        } catch (Exception e) {
            throw new ServiceException("获取点赞列表失败!");
        }
    }

    /**
     *  获取点赞用户ID列表（按时间逆序）
     */
    @GetMapping("/api/content-service/giveLikeListD")
    public Result<Set<Long>> giveLikeListD(@RequestParam("postId") Long postId) {
        try {
            String key = ContentRedisKey.POST_LIKE_KEY + postId;
            RScoredSortedSet<Long> likeSet = redissonClient.getScoredSortedSet(key);
            // 按时间顺序返回用户ID列表
            Collection<Long> longs = likeSet.valueRangeReversed(0, -1);
            return Results.success((Set<Long>) longs);
        } catch (Exception e) {
            throw new ServiceException("获取点赞列表失败!");
        }
    }


    /**
     * 获取点赞数量
     */
    @GetMapping("/api/content-service/getLikeCount")
    public Result<Integer> getLikeCount(@RequestParam("postId") Long postId) {
        try {
            String key = ContentRedisKey.POST_LIKE_KEY + postId;
            RScoredSortedSet<Long> likeSet = redissonClient.getScoredSortedSet(key);
            return Results.success(likeSet.size());
        } catch (Exception e) {
            throw new ServiceException("获取点赞数量失败!");
        }
    }




}
