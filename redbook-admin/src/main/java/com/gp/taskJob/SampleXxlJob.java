package com.gp.taskJob;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gp.common.constant.ContentRedisKey;
import com.gp.entity.*;
import com.gp.es.esMapping.LoginLogsMapping;
import com.gp.es.esMapping.OperaLogsMapping;
import com.gp.es.esMapping.postMapping;
import com.gp.es.service.LoginInfoRepository;
import com.gp.es.service.OperaInfoRepository;
import com.gp.es.service.PostInfoRepository;
import com.gp.mapper.*;
import com.gp.service.TCollectionService;
import com.gp.service.TLikesService;
import com.gp.utils.RedisUtils;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.gp.constant.TaskConstant.*;

/**
 * XxlJob开发示例（Bean模式）
 *
 * 开发步骤：
 *      1、任务开发：在Spring Bean实例中，开发Job方法；
 *      2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 *      3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 *      4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 * @author xuxueli 2019-12-11 21:52:51
 */
@Component
@RequiredArgsConstructor
public class SampleXxlJob {
    private static Logger logger = LoggerFactory.getLogger(SampleXxlJob.class);

    private final TPostsMapper postsMapper;

    private final TObjTagsMapper postTagsMapper;

    private final TTagsMapper tagsMapper;

    private final TLoginLogMapper loginLogMapper;

    private final TOperationLogMapper operationLogMapper;

    private final TCollectionService collectionService;

    private final TLikesService likesService;

    private final RedissonClient redissonClient;

    private final PostInfoRepository postInfoRepository;

    private final LoginInfoRepository loginInfoRepository;

    private final OperaInfoRepository operaInfoRepository;




    /***********************************************文章同步************************************************************/


    /**
     * 1、进行文章同步es操作
     */
    @XxlJob("AddPostJobHandler")
    public void AddPostJobHandler() throws Exception {
        // 获取上次任务执行时间
        String lastExecutionTimeStr = RedisUtils.getCacheObject(TASK_POST_KEY);
        LocalDateTime lastExecutionTime;
        if (lastExecutionTimeStr != null) {
            try {
                lastExecutionTime = LocalDateTime.parse(lastExecutionTimeStr); // 解析时间
            } catch (Exception e) {
                lastExecutionTime = LocalDateTime.of(1970, 1, 1, 0, 0); // 解析失败使用默认值
            }
        } else {
            lastExecutionTime = LocalDateTime.of(1970, 1, 1, 0, 0); // 第一次执行使用默认值
        }
        List<TPosts> postsList = postsMapper.selectList(Wrappers.lambdaQuery(TPosts.class).gt(TPosts::getUpdateTime, lastExecutionTime).orderByDesc(TPosts::getCreateTime));
        postsList.stream().forEach(s->{
            postMapping postMapping = new postMapping();
            postMapping.setPostId(s.getPostId());
            postMapping.setContent(s.getContent());
            postMapping.setTitleSuggest(s.getContent());
            postMapping.setTitle(s.getTitle());
            postMapping.setImage(s.getImage());
            postMapping.setCreateTime(s.getCreateTime());
            postMapping.setUpdateTime(s.getUpdateTime());
            postMapping.setUserName(s.getUsername());
            String key = ContentRedisKey.POST_LIKE_KEY + s.getPostId();
            RScoredSortedSet<Long> likeSet = redissonClient.getScoredSortedSet(key);
            postMapping.setLikeCount(likeSet.size());
            LambdaQueryWrapper<TObjTags> wrapper = Wrappers.lambdaQuery(TObjTags.class).eq(TObjTags::getObjId, s.getPostId()).eq(TObjTags::getType, 1);
            List<String> tagList = new ArrayList<>();
            List<TObjTags> tObjTags = postTagsMapper.selectList(wrapper);
            for (int i = 0; i < tObjTags.size(); i++) {
                TTags tags = tagsMapper.selectById(tObjTags.get(i).getTagId());
                tagList.add(String.valueOf(tags.getTagId()));
            }
            postMapping.setTags(tagList);
            postMapping.setUserId(s.getUserId());
            postInfoRepository.save(postMapping);
        });
        RedisUtils.setCacheObject(TASK_POST_KEY, LocalDateTime.now().toString());

    }


    /**
     * 2、进行文章点赞同步数据库操作
     */
    @XxlJob("AddLikeJobHandler")
    public void AddLikeJobHandler() throws Exception {
        List<Object> events = redissonClient.getList(POST_LIKE_EVENT_KEY).readAll();
        List<TLikes> likesToInsert = new ArrayList<>();
        List<String> eventsToRemove = new ArrayList<>();
        for (Object event : events) {
            String[] parts = event.toString().split(":");
            Long postId = Long.valueOf(parts[0]);
            Long userId = Long.valueOf(parts[1]);
            int type = Integer.valueOf(parts[2]);
            if (type == 1) { // 点赞
                TLikes like = TLikes.builder()
                        .objId(postId)
                        .userId(userId)
                        .likeType(1)
                        .build();
                likesToInsert.add(like);
            } else if (type == 2) { // 取消点赞
                likesService.deleteLikeByUserIdAndPostId(userId, postId);
            }
            eventsToRemove.add(event.toString());
        }
        // 批量插入
        if (!likesToInsert.isEmpty()) {
            likesService.saveBatch(likesToInsert);
        }
        // 清理处理过的事件
        redissonClient.getList(POST_LIKE_EVENT_KEY).removeAll(eventsToRemove);
    }

    /**
     * 3、进行文章收藏同步数据库操作
     */
    @XxlJob("AddCollectionJobHandler")
    public void AddCollectionJobHandler() throws Exception {
        List<Object> events = redissonClient.getList(POST_COLLECT_EVENT_KEY).readAll();
        List<TCollection> collectionsToInsert = new ArrayList<>();
        List<String> eventsToRemove = new ArrayList<>();
        for (Object event : events) {
            String[] parts = event.toString().split(":");
            Long postId = Long.valueOf(parts[0]);
            Long userId = Long.valueOf(parts[1]);
            int type = Integer.valueOf(parts[2]);
            if (type == 1) { // 点赞
                TCollection collection = TCollection.builder()
                        .objId(postId)
                        .userId(userId)
                        .collectionType(1)
                        .build();
                collectionsToInsert.add(collection);
            } else if (type == 2) { // 取消点赞
                collectionService.deleteCollectByUserIdAndPostId(userId, postId);
            }
            eventsToRemove.add(event.toString());
        }
        // 批量插入
        if (!collectionsToInsert.isEmpty()) {
            collectionService.saveBatch(collectionsToInsert);
        }
        // 清理处理过的事件
        redissonClient.getList(POST_COLLECT_EVENT_KEY).removeAll(eventsToRemove);
    }





    /************************************************视频同步************************************************************/



    /**
     * 1、进行点赞同步数据库操作
     */
    @XxlJob("AddVideoLikeJobHandler")
    public void AddVideoLikeJobHandler() throws Exception {
        List<Object> events = redissonClient.getList(VIDEO_LIKE_EVENT_KEY).readAll();
        List<TLikes> likesToInsert = new ArrayList<>();
        List<String> eventsToRemove = new ArrayList<>();
        for (Object event : events) {
            String[] parts = event.toString().split(":");
            Long videoId = Long.valueOf(parts[0]);
            Long userId = Long.valueOf(parts[1]);
            int type = Integer.valueOf(parts[2]);
            if (type == 1) { // 点赞
                TLikes like = TLikes.builder()
                        .objId(videoId)
                        .userId(userId)
                        .likeType(2)
                        .build();
                likesToInsert.add(like);
            } else if (type == 2) { // 取消点赞
                likesService.deleteLikeByUserIdAndPostId(userId, videoId);
            }
            eventsToRemove.add(event.toString());
        }
        // 批量插入
        if (!likesToInsert.isEmpty()) {
            likesService.saveBatch(likesToInsert);
        }
        // 清理处理过的事件
        redissonClient.getList(POST_LIKE_EVENT_KEY).removeAll(eventsToRemove);
    }

    /**
     * 2、进行视频收藏同步数据库操作
     */
    @XxlJob("AddVideoCollectionJobHandler")
    public void AddVideoCollectionJobHandler() throws Exception {
        List<Object> events = redissonClient.getList(VIDEO_COLLECT_EVENT_KEY).readAll();
        List<TCollection> collectionsToInsert = new ArrayList<>();
        List<String> eventsToRemove = new ArrayList<>();
        for (Object event : events) {
            String[] parts = event.toString().split(":");
            Long videoId = Long.valueOf(parts[0]);
            Long userId = Long.valueOf(parts[1]);
            int type = Integer.valueOf(parts[2]);
            if (type == 1) { // 点赞
                TCollection collection = TCollection.builder()
                        .objId(videoId)
                        .userId(Long.valueOf(userId))
                        .collectionType(2)
                        .build();
                collectionsToInsert.add(collection);
            } else if (type == 2) { // 取消点赞
                collectionService.deleteCollectByUserIdAndPostId(userId, videoId);
            }
            eventsToRemove.add(event.toString());
        }
        // 批量插入
        if (!collectionsToInsert.isEmpty()) {
            collectionService.saveBatch(collectionsToInsert);
        }
        // 清理处理过的事件
        redissonClient.getList(POST_COLLECT_EVENT_KEY).removeAll(eventsToRemove);

    }



    /************************************************日志同步************************************************************/

    /**
     * 1、进行登录日志同步es操作
     */
    @XxlJob("AddLoginLogsJobHandler")
    public void AddLoginLogsJobHandler() throws Exception {
        // 获取上次任务执行时间
        String lastExecutionTimeStr = RedisUtils.getCacheObject(TASK_LOGIN_KEY);
        LocalDateTime lastExecutionTime;
        if (lastExecutionTimeStr != null) {
            try {
                lastExecutionTime = LocalDateTime.parse(lastExecutionTimeStr); // 解析时间
            } catch (Exception e) {
                lastExecutionTime = LocalDateTime.of(1970, 1, 1, 0, 0); // 解析失败使用默认值
            }
        } else {
            lastExecutionTime = LocalDateTime.of(1970, 1, 1, 0, 0); // 第一次执行使用默认值
        }
        List<TLoginLog> loginList = loginLogMapper.selectList(Wrappers.lambdaQuery(TLoginLog.class).gt(TLoginLog::getUpdateTime, lastExecutionTime).orderByDesc(TLoginLog::getCreateTime));
        loginList.stream().forEach(s->{
            LoginLogsMapping loginLogsMapping = new LoginLogsMapping();
            loginLogsMapping.setId(s.getInfoId());
            loginLogsMapping.setLoginLocation(s.getLoginLocation());
            loginLogsMapping.setLoginTime(s.getLoginTime());
            loginLogsMapping.setOs(s.getOs());
            loginLogsMapping.setUserName(s.getUserName());
            loginLogsMapping.setBrowser(s.getBrowser());
            loginLogsMapping.setIpaddr(s.getIpaddr());
            loginLogsMapping.setMsg(s.getMsg());
            loginLogsMapping.setStatus(s.getStatus());
            loginInfoRepository.save(loginLogsMapping);
        });
        RedisUtils.setCacheObject(TASK_LOGIN_KEY, LocalDateTime.now().toString());

    }


    /**
     * 2、进行操作日志同步es操作
     */
    @XxlJob("AddOperaLogsJobHandler")
    public void AddOperaLogsJobHandler() throws Exception {
        // 获取上次任务执行时间
        String lastExecutionTimeStr = RedisUtils.getCacheObject(TASK_OPERA_KEY);
        LocalDateTime lastExecutionTime;
        if (lastExecutionTimeStr != null) {
            try {
                lastExecutionTime = LocalDateTime.parse(lastExecutionTimeStr); // 解析时间
            } catch (Exception e) {
                lastExecutionTime = LocalDateTime.of(1970, 1, 1, 0, 0); // 解析失败使用默认值
            }
        } else {
            lastExecutionTime = LocalDateTime.of(1970, 1, 1, 0, 0); // 第一次执行使用默认值
        }
        List<TOperationLog> operaList = operationLogMapper.selectList(Wrappers.lambdaQuery(TOperationLog.class).gt(TOperationLog::getUpdateTime, lastExecutionTime).orderByDesc(TOperationLog::getCreateTime));
        operaList.stream().forEach(s->{
            OperaLogsMapping operaLogsMapping = new OperaLogsMapping();
            operaLogsMapping.setId(s.getOperId());
            operaLogsMapping.setMethod(s.getMethod());
            operaLogsMapping.setBusinessType(s.getBusinessType());
            operaLogsMapping.setCostTime(s.getCostTime());
            operaLogsMapping.setJsonResult(s.getJsonResult());
            operaLogsMapping.setStatus(s.getStatus());
            operaLogsMapping.setOperIp(s.getOperIp());
            operaLogsMapping.setOperLocation(s.getOperLocation());
            operaLogsMapping.setErrorMsg(s.getErrorMsg());
            operaLogsMapping.setOperTime(s.getOperTime());
            operaLogsMapping.setOperatorType(s.getOperatorType());
            operaLogsMapping.setOperUrl(s.getOperUrl());
            operaLogsMapping.setUserId(s.getUserId());
            operaLogsMapping.setOperName(s.getOperName());
            operaLogsMapping.setTitle(s.getTitle());
            operaLogsMapping.setStatus(s.getStatus());
            operaInfoRepository.save(operaLogsMapping);
        });
        RedisUtils.setCacheObject(TASK_OPERA_KEY, LocalDateTime.now().toString());
    }



    public void init(){
        logger.info("init");
    }
    public void destroy(){
        logger.info("destroy");
    }


}
