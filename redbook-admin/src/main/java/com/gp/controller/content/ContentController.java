/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gp.controller.content;



import cn.dev33.satoken.annotation.SaCheckDisable;
import com.gp.dto.req.content.AddPostDao;
import com.gp.dto.req.content.ForwardPostDao;
import com.gp.dto.req.content.PostListDao;
import com.gp.dto.resp.content.PostListVo;
import com.gp.entity.TTags;
import com.gp.es.esMapping.postMapping;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TPostForwardService;
import com.gp.service.TPostsService;
import com.gp.service.TTagsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 内容控制层
 */
@RestController
@RequiredArgsConstructor
public class ContentController {

    private final TPostsService postsService;

    private final TTagsService tagsService;

    private final TPostForwardService postForwardService;

    private final ElasticsearchTemplate elasticsearchTemplate;



    /**
     * 搜索框联想功能
     */
    @GetMapping("/api/content-service/suggestions")
    public Result<List<String>> suggestions(@RequestParam("prefix") String prefix) {
        // 创建查询条件
        Criteria criteria = Criteria.where("titleSuggest").startsWith(prefix);

        // 构建 CriteriaQuery
        CriteriaQuery query = new CriteriaQuery(criteria);
        // 按照相关度 (_score) 降序排序
        query.addSort(Sort.by(Sort.Order.desc("_score")));

        // 分页：只返回前 10 条数据
        query.setPageable(PageRequest.of(0, 10));

        // 执行查询
        SearchHits<postMapping> searchHits = elasticsearchTemplate.search(query, postMapping.class);
        // 提取查询结果并返回候选标题
        List<String> collect = searchHits.stream()
                .map(hit -> hit.getContent().getContent())
                .collect(Collectors.toList());
        return Results.success(collect);
    }


    /**
     * 获取文章列表信息
     */
    @PostMapping("/api/content-service/findPostList")
    public Result<PageResponse<PostListVo>> findPostList(@RequestBody PostListDao requestParam) {
        return Results.success(postsService.findPostList(requestParam));
    }

    /**
     * 获取文章信息
     */
    @GetMapping("/api/content-service/findPostById")
    public Result<PostListVo> findPostById(@RequestParam("postId") Long postId) {
        return Results.success(postsService.findPostById(postId));
    }

    /**
     * 我的--》文章
     */
    @PostMapping("/api/content-service/findPostInfo")
    public Result<PageResponse<PostListVo>> findPostInfo(@RequestBody PostListDao requestParam) {
        return Results.success(postsService.findPostInfo(requestParam));
    }


    /**
     * 获取标签信息
     */
    @GetMapping("/api/content-service/findTagList")
    public Result<List<TTags>> findTagList() {
        return Results.success(tagsService.findTagList());
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/api/content-service/deletePost")
    public Result deletePost(@RequestParam("postId") Long postId) {
        return Results.success(postsService.removePost(postId));
    }

    /**
     * 新增文章
     */
    @SaCheckDisable("addPost")
    @PostMapping("/api/content-service/addPost")
    public Result addPost(@RequestBody AddPostDao requestParam) {
        return Results.success(postsService.addPost(requestParam));
    }


    /**
     * 修改文章
     */
    @PostMapping("/api/content-service/updatePost")
    public Result updatePost(@RequestBody AddPostDao requestParam) {
        return Results.success(postsService.updatePost(requestParam));
    }


    /**
     * 转发文章
     */
    @PostMapping("/api/content-service/forwardPost")
    public Result forwardPost(@RequestBody ForwardPostDao requestParam) {
        return Results.success(postForwardService.forwardPost(requestParam));
    }


}
