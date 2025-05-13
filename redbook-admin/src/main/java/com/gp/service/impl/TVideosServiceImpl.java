package com.gp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.dto.req.content.VideoDo;
import com.gp.dto.req.content.VideoListDo;
import com.gp.dto.resp.content.VideoVo;
import com.gp.dto.resp.user.UserInfo;
import com.gp.entity.TCollection;
import com.gp.entity.TLikes;
import com.gp.entity.TVideos;
import com.gp.es.esMapping.VideoMapping;
import com.gp.listener.event.DeleteEvent;
import com.gp.mapper.TVideosMapper;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.*;
import com.gp.utils.PageUtil;
import com.gp.utils.SpringUtils;
import com.gp.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
* @author Administrator
* @description 针对表【t_videos(视频表)】的数据库操作Service实现
* @createDate 2024-12-23 20:58:29
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class TVideosServiceImpl extends ServiceImpl<TVideosMapper, TVideos>
    implements TVideosService{

    private final TVideosMapper videosMapper;

    private final TCollectionService collectionService;

    private final TLikesService likesService;

    private final TVideoWatchLogService videoWatchLogService;

    private final ElasticsearchTemplate elasticsearchTemplate;

    private final TUserService userService;


    @Override
    public Result addMedium(VideoDo requestParam) {
        TVideos videos = BeanUtil.toBean(requestParam, TVideos.class);
        videos.setStatus(0);
        if(videos.getVideoId()==null){
            videosMapper.insert(videos);
        }else {
            videosMapper.updateById(videos);
        }
        return Results.success();
    }

    @Override
    public Result deleteMediumById(Long videoId) {
        int count = videosMapper.deleteById(videoId);
        if(count>0){
            SpringUtils.context().publishEvent(new DeleteEvent(this,videoId, "video"));
        }
        return Results.success();
    }

    @Override
    public PageResponse<VideoVo> findVideoList(VideoListDo requestParam) {
        ArrayList<VideoVo> arrayList = new ArrayList<>();
        Page page = PageUtil.convert(requestParam);
        int current =(int) page.getCurrent();
        int size = (int) page.getSize();
        Criteria criteria = new Criteria();
        if(StringUtils.isNotEmpty(requestParam.getTitle())){
            criteria = criteria.and("title").contains(requestParam.getTitle());
        }
        if(StringUtils.isNotEmpty(requestParam.getUsername())){
            criteria = criteria.and("username").contains(requestParam.getUsername());
        }
        /*if(ObjectUtil.isNotNull(requestParam.getUserId())){
            criteria = criteria.and("userId").contains(String.valueOf(requestParam.getUserId()));
        }*/
        // 根据 isCollection 添加查询条件,收藏列表
        Boolean isLike = requestParam.getIsLike();
        if (isLike != null && isLike) {
            // 获取用户喜欢列表
            List<TLikes> likeList = likesService.findLikeList(requestParam.getUserId(), 2);
            List<Long> likeIds = likeList.stream()
                    .map(TLikes::getObjId)
                    .collect(Collectors.toList());
            // 如果 postId 列表不为空，将其加入查询条件
            if (!CollectionUtils.isEmpty(likeIds)) {
                criteria = criteria.and("videoId").in(likeIds);
            }else {
                return PageResponse.<VideoVo>builder()
                        .current(page.getCurrent())
                        .size(page.getSize())
                        .records(arrayList)
                        .total(0l)
                        .build();
            }
        }
        // 分页
        Query query = new CriteriaQuery(criteria)
                .setPageable(PageRequest.of(current - 1, size));
        SearchHits<VideoMapping> searchHits = elasticsearchTemplate.search(query, VideoMapping.class);
        long totalValue = searchHits.getTotalHits();
        for (SearchHit<VideoMapping> searchHit : searchHits.getSearchHits()) {
            VideoMapping account = searchHit.getContent(); // 这就是得到的实体类
            VideoVo convert = BeanUtil.toBean(account, VideoVo.class);
            arrayList.add(convert);
        }

        Set<Long> userIds = arrayList.stream()
                .map(VideoVo::getUserId)
                .collect(Collectors.toSet());

        // 调用用户服务批量获取用户信息
        Map<Long, UserInfo> userInfoMap = userService.getUserInfoByIds(userIds);

        arrayList.stream().forEach(videoVo -> {
            //查询文章是否点赞,收藏
            if (videoVo.getVideoId() != null) {
                LambdaQueryWrapper<TLikes> likeWrapper = Wrappers.lambdaQuery(TLikes.class)
                        .eq(TLikes::getObjId, videoVo.getVideoId())
                        .eq(TLikes::getUserId, requestParam.getUserId())
                        .eq(TLikes::getLikeType, 2);
                long count = likesService.count(likeWrapper);
                videoVo.setIsLike(count==0 ? false : true);

                LambdaQueryWrapper<TCollection> collectWrapper = Wrappers.lambdaQuery(TCollection.class)
                        .eq(TCollection::getObjId, videoVo.getVideoId())
                        .eq(TCollection::getUserId, requestParam.getUserId())
                        .eq(TCollection::getCollectionType, 2);
                long count2 = collectionService.count(collectWrapper);
                videoVo.setIsCollection(count2==0 ? false : true);

                videoVo.setAvatar(userInfoMap.get(videoVo.getUserId()).getUserPic());
                videoVo.setUsername(userInfoMap.get(videoVo.getUserId()).getUsername());
            }

        });
        return PageResponse.<VideoVo>builder()
                .current(page.getCurrent())
                .size(page.getSize())
                .records(arrayList)
                .total(totalValue)
                .build();

        // 查询用户观看过的视频 ID
   /*     List<Long> watchedVideoIds = videoWatchLogService.getWatchedVideosFromRedis(requestParam.getUserId());
        if (watchedVideoIds != null && !watchedVideoIds.isEmpty()) {
            queryWrapper.notIn(TVideos::getVideoId,watchedVideoIds);
        }*/
    }

    @Override
    public PageResponse<VideoVo> findMediumList(VideoListDo requestParam) {
        // 获取分页的视频数据
        LambdaQueryWrapper<TVideos> queryWrapper = Wrappers.lambdaQuery(TVideos.class)
                .like(StringUtils.isNotEmpty(requestParam.getTitle()),TVideos::getTitle, requestParam.getTitle())
                .like(StringUtils.isNotEmpty(requestParam.getUsername()),TVideos::getUsername, requestParam.getUsername())
                .eq(requestParam.getUserId() != null,TVideos::getUserId, requestParam.getUserId())
                .eq(requestParam.getStatus() != null,TVideos::getStatus, requestParam.getStatus());
        if(requestParam.getIsCollection()){
            // 获取用户收藏列表
            List<TCollection> collectionList = collectionService.findCollectionList(requestParam.getUserId(),2);
            // 提取所有的 postId
            List<Long> videoIds = collectionList.stream()
                    .map(TCollection::getObjId)
                    .collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(videoIds)){
                queryWrapper.in(TVideos::getVideoId, videoIds);
            }else {
                Page page = PageUtil.convert(requestParam);
                ArrayList<VideoVo> arrayList = new ArrayList<>();
                return PageResponse.<VideoVo>builder()
                        .current(page.getCurrent())
                        .size(page.getSize())
                        .records(arrayList)
                        .total(0l)
                        .build();
            }
        }

        queryWrapper.orderByDesc(TVideos::getCreateTime);
        IPage<TVideos> videosPage = videosMapper.selectPage(PageUtil.convert(requestParam), queryWrapper);
        return PageUtil.convert(videosPage, each -> {
            VideoVo videoVo = BeanUtil.toBean(each, VideoVo.class);
            return videoVo;
        });
    }
}




