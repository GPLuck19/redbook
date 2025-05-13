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

package com.gp.controller.goods;


import com.gp.dto.req.goods.Goods;
import com.gp.dto.req.goods.GoodsDao;
import com.gp.dto.resp.goods.GoodsVo;
import com.gp.entity.TGoods;
import com.gp.es.esMapping.GoodsMapping;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TGoodsService;
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
 * 商品服务控制层
 */
@RestController
@RequiredArgsConstructor
public class GoodsController {

    private final TGoodsService goodsService;

    private final ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 搜索框联想功能
     */
    @GetMapping("/api/goods-service/suggestions")
    public Result<List<String>> suggestions(@RequestParam("prefix") String prefix) {
        // 创建查询条件
        Criteria criteria = Criteria.where("nameSuggest").startsWith(prefix);

        // 构建 CriteriaQuery
        CriteriaQuery query = new CriteriaQuery(criteria);
        // 按照相关度 (_score) 降序排序
        query.addSort(Sort.by(Sort.Order.desc("_score")));

        // 分页：只返回前 10 条数据
        query.setPageable(PageRequest.of(0, 10));

        // 执行查询
        SearchHits<GoodsMapping> searchHits = elasticsearchTemplate.search(query, GoodsMapping.class);
        // 提取查询结果并返回候选标题
        List<String> collect = searchHits.stream()
                .map(hit -> hit.getContent().getGoodsname())
                .collect(Collectors.toList());
        return Results.success(collect);
    }


    /**
     * 我的-》商品列表查看
     */
    @PostMapping("/api/goods-service/findGoods")
    public Result<PageResponse<GoodsVo>> findGoods(@RequestBody Goods requestParam) {
        return Results.success(goodsService.findGoods(requestParam));
    }

    /**
     * 瀑布流商品列表查看
     */
    @PostMapping("/api/goods-service/findGoodsList")
    public Result<PageResponse<GoodsVo>> findGoodsList(@RequestBody Goods requestParam) {
        return Results.success(goodsService.findGoodsList(requestParam));
    }


    /**
     * 获取商品信息
     */
    @GetMapping("/api/goods-service/findGoodsById")
    public Result<TGoods> findGoodsById(@RequestParam("goodsId") Long goodsId) {
        return Results.success(goodsService.findGoodsById(goodsId));
    }


    /**
     * 商品保存
     */
    @PostMapping("/api/goods-service/saveOrUpdateGoods")
    public Result saveOrUpdateGoods(@RequestBody GoodsDao requestParam) {
        return Results.success(goodsService.saveOrUpdateGoods(requestParam));
    }


    /**
     * 商品上下架
     */
    @PostMapping("/api/goods-service/isPubGoods")
    public Result isPubGoods(@RequestParam("goodsId") Long goodsId,@RequestParam("isPub") Integer isPub) {
        return Results.success(goodsService.isPubGoods(goodsId,isPub));
    }

    /**
     * 商品移除
     */
    @DeleteMapping("/api/goods-service/deleteGoods")
    public Result deleteGoods(@RequestParam("goodsId") Long goodsId) {
        return Results.success(goodsService.deleteGoods(goodsId));
    }

}
