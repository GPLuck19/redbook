package com.gp.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.dto.req.content.AddPostDao;
import com.gp.dto.req.content.PostListDao;
import com.gp.dto.resp.content.PostListVo;
import com.gp.entity.TCollection;
import com.gp.entity.TObjTags;
import com.gp.entity.TPosts;
import com.gp.entity.TTags;
import com.gp.es.esMapping.postMapping;
import com.gp.es.service.PostInfoRepository;
import com.gp.exception.ServiceException;
import com.gp.mapper.TObjTagsMapper;
import com.gp.mapper.TPostsMapper;
import com.gp.mapper.TTagsMapper;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.*;
import com.gp.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【t_posts(文章表)】的数据库操作Service实现
* @createDate 2024-11-21 09:50:24
*/
@Service
@RequiredArgsConstructor
public class TPostsServiceImpl extends ServiceImpl<TPostsMapper, TPosts>
    implements TPostsService{

    private final ElasticsearchTemplate elasticsearchTemplate;

    private final PostInfoRepository postInfoRepository;

    private final TObjTagsService postTagsService;

    private final TCommentsService commentsService;

    private final TCollectionService collectionService;

    private final TLikesService likesService;

    private final TPostsMapper postsMapper;

    private final TObjTagsMapper postTagsMapper;

    private final TTagsMapper tagsMapper;




    @Override
    public PageResponse<PostListVo> findPostList(PostListDao requestParam) {
        ArrayList<PostListVo> arrayList = new ArrayList<>();
        Page page = PageUtil.convert(requestParam);
        int current =(int) page.getCurrent();
        int size = (int) page.getSize();
        Criteria criteria = new Criteria();
        if(StringUtils.isNotEmpty(requestParam.getContent())){
            criteria = criteria.and("content").contains(requestParam.getContent());
        }
        List<Long> tagIdList = requestParam.getTagIdList();
        if (!CollectionUtils.isEmpty(tagIdList)) {
            criteria = criteria.and("tags").in(tagIdList);
        }
        // 根据 isCollection 添加查询条件,收藏列表
        Boolean isCollection = requestParam.getIsCollection();
        if (isCollection != null && isCollection) {
            // 获取用户收藏列表
            List<TCollection> collectionList = collectionService.findCollectionList(requestParam.getUserId(),1);
            // 提取所有的 postId
            List<Long> postIds = collectionList.stream()
                    .map(TCollection::getObjId)
                    .collect(Collectors.toList());
            // 如果 postId 列表不为空，将其加入查询条件
            if (!CollectionUtils.isEmpty(postIds)) {
                criteria = criteria.and("postId").in(postIds);
            }else {
                return PageResponse.<PostListVo>builder()
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
        SearchHits<postMapping> searchHits = elasticsearchTemplate.search(query, postMapping.class);
        long totalValue = searchHits.getTotalHits();
        for (SearchHit<postMapping> searchHit : searchHits.getSearchHits()) {
            postMapping account = searchHit.getContent(); // 这就是得到的实体类
            PostListVo convert = BeanUtil.toBean(account, PostListVo.class);
            arrayList.add(convert);
        }
        return PageResponse.<PostListVo>builder()
                .current(page.getCurrent())
                .size(page.getSize())
                .records(arrayList)
                .total(totalValue)
                .build();
    }

    @Override
    @Transactional
    public Result removePost(Long postId) {
        postTagsService.deletePostTagById(postId,1);
        commentsService.deleteCommentsById(postId);
        likesService.deleteLikeById(postId);
        collectionService.deletePostCollectionById(postId);
        boolean remove = this.removeById(postId);
        postInfoRepository.deleteById(String.valueOf(postId));
        if(remove){
            return Results.success();
        }
        throw new ServiceException("文章删除失败!");
    }

    @Override
    @Transactional
    public Result addPost(AddPostDao requestParam) {
        TPosts posts = TPosts.builder().image(requestParam.getImages().toString())
                .title(requestParam.getTitle())
                .content(requestParam.getContent())
                .username(requestParam.getUsername())
                .userId(requestParam.getUserId()).build();
        posts.setCreateTime(new Date());
        boolean save = this.save(posts);
        postTagsService.addPostTag(posts.getPostId(),requestParam.getTagIdList(),1);
        if(save){
            return Results.success();
        }
        throw new ServiceException("文章保存失败!");
    }

    @Override
    public PageResponse<PostListVo> findPostInfo(PostListDao requestParam) {
        LambdaQueryWrapper<TPosts> queryWrapper = Wrappers.lambdaQuery(TPosts.class)
                .orderByDesc(TPosts::getCreateTime);
        if(ObjectUtil.isNotEmpty(requestParam.getUserId())){
            queryWrapper.eq(TPosts::getUserId, requestParam.getUserId());
        }
        if(ObjectUtil.isNotEmpty(requestParam.getContent())){
            queryWrapper.like(TPosts::getContent, requestParam.getContent());
        }
        IPage<TPosts> postsPage = postsMapper.selectPage(PageUtil.convert(requestParam), queryWrapper);
        return PageUtil.convert(postsPage, each -> {
            LambdaQueryWrapper<TObjTags> wrapper = Wrappers.lambdaQuery(TObjTags.class).eq(TObjTags::getObjId, each.getPostId()).eq(TObjTags::getType, 1);
            List<String> tagList = new ArrayList<>();
            List<TObjTags> tObjTags = postTagsMapper.selectList(wrapper);
            for (int i = 0; i < tObjTags.size(); i++) {
                TTags tags = tagsMapper.selectById(tObjTags.get(i).getTagId());
                tagList.add(String.valueOf(tags.getTagId()));
            }
            PostListVo actualResult = BeanUtil.toBean(each, PostListVo.class);
            actualResult.setTagList(tagList);
            return actualResult;
        });
    }

    @Override
    @Transactional
    public Result updatePost(AddPostDao requestParam) {
        try {
            TPosts posts = this.getById(requestParam.getId());
            BeanUtil.copyProperties(requestParam,posts);
            posts.setImage(requestParam.getImages().toString());
            this.updateById(posts);
            //更新标签内容
            postTagsService.deletePostTagById(requestParam.getId(),1);
            postTagsService.addPostTag(posts.getPostId(),requestParam.getTagIdList(),1);
            return Results.success();
        } catch (Exception e) {
            throw new ServiceException("文章修改失败!");
        }
    }

    @Override
    public PostListVo findPostById(Long postId) {
        Optional<postMapping> post = postInfoRepository.findById(String.valueOf(postId));
        PostListVo convert=null;
        if (post.isPresent()) {
            postMapping postMapping = post.get();
            // 根据 postMapping 对象中的数据构建 PostListRespDto 对象
            convert = BeanUtil.toBean(postMapping, PostListVo.class);
            convert.setTagList(postMapping.getTags());
        }
        return convert;
    }


}




