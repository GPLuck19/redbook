package com.gp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.dto.req.goods.Goods;
import com.gp.dto.req.goods.GoodsDao;
import com.gp.dto.resp.goods.GoodsVo;
import com.gp.entity.*;
import com.gp.enums.GoodsEnum;
import com.gp.es.esMapping.GoodsMapping;
import com.gp.exception.ServiceException;
import com.gp.listener.event.DeleteEvent;
import com.gp.mapper.TGoodsMapper;
import com.gp.mapper.TTagsMapper;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TObjTagsService;
import com.gp.service.TGoodsService;
import com.gp.utils.PageUtil;
import com.gp.utils.SpringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



/**
* @author Administrator
* @description 针对表【tb_goods(商品表)】的数据库操作Service实现
* @createDate 2024-10-29 10:12:51
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class TGoodsServiceImpl extends ServiceImpl<TGoodsMapper, TGoods>
    implements TGoodsService {

    private final TGoodsMapper goodsMapper;

    private final TObjTagsService objTagsService;

    private final TTagsMapper tagsMapper;

    private final ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public PageResponse<GoodsVo> findGoods(Goods requestParam) {
        LambdaQueryWrapper<TGoods> queryWrapper = Wrappers.lambdaQuery(TGoods.class)
                .eq(TGoods::getUserId,requestParam.getUserId())
                .orderByDesc(TGoods::getCreateTime);
        if(ObjectUtil.isNotEmpty(requestParam.getGoodsname())){
            queryWrapper.like(TGoods::getGoodsname, requestParam.getGoodsname());
        }
        IPage<TGoods> goodsPage = goodsMapper.selectPage(PageUtil.convert(requestParam), queryWrapper);
        return PageUtil.convert(goodsPage, each -> {
            LambdaQueryWrapper<TObjTags> wrapper = Wrappers.lambdaQuery(TObjTags.class).eq(TObjTags::getObjId, each.getId()).eq(TObjTags::getType, 2);
            List<Long> tagList = new ArrayList<>();
            List<TObjTags> tObjTags = objTagsService.list(wrapper);
            for (int i = 0; i < tObjTags.size(); i++) {
                TTags tags = tagsMapper.selectById(tObjTags.get(i).getTagId());
                tagList.add(tags.getTagId());
            }
            GoodsVo actualResult = BeanUtil.toBean(each, GoodsVo.class);
            actualResult.setTags(tagList);
            return actualResult;
        });
    }


    @Override
    @Transactional
    public Result saveOrUpdateGoods(GoodsDao requestParam) {
        TGoods goods = new TGoods();
        BeanUtil.copyProperties(requestParam, goods);
        try {
            if(goods.getId()==null){
                goodsMapper.insert(goods);
                objTagsService.addPostTag(goods.getId(),requestParam.getTags(),2);
            }else {
                goodsMapper.updateById(goods);
                objTagsService.deletePostTagById(requestParam.getId(),2);
                objTagsService.addPostTag(goods.getId(),requestParam.getTags(),2);
            }
            if(goods.getIspub()==1){
                SpringUtils.context().publishEvent(goods);
            }
        } catch (Exception e) {
            throw new ServiceException(GoodsEnum.GOODS_BAD.getMsg());
        }
        return Results.success("操作成功！");
    }

    @Override
    public Result isPubGoods(Long goodsId,Integer isPub) {
        TGoods goods = this.getById(goodsId);
        goods.setIspub(isPub);
        boolean update = this.updateById(goods);
        if(update){
            if(isPub==1){
                SpringUtils.context().publishEvent(goods);
            }else {
                SpringUtils.context().publishEvent(new DeleteEvent(this, goods.getId(),"goods"));
            }
        }
        return Results.success();
    }

    @Override
    public PageResponse<GoodsVo> findGoodsList(Goods requestParam) {
        ArrayList<GoodsVo> arrayList = new ArrayList<>();
        Page page = PageUtil.convert(requestParam);
        int current =(int) page.getCurrent();
        int size = (int) page.getSize();
        Criteria criteria = new Criteria();
        if(StringUtils.isNotEmpty(requestParam.getGoodsname())){
            criteria = criteria.and("goodsname").contains(requestParam.getGoodsname());
        }
        List<Long> tagIdList = requestParam.getTags();
        if (!CollectionUtils.isEmpty(tagIdList)) {
            criteria = criteria.and("tags").in(tagIdList);
        }
        // 价格范围查询
        BigDecimal minPrice = requestParam.getMinPrice();
        BigDecimal maxPrice = requestParam.getMaxPrice();
        if (minPrice != null && maxPrice != null) {
            criteria = criteria.and("price").between(minPrice, maxPrice);
        } else if (minPrice != null) {
            criteria = criteria.and("price").greaterThanEqual(minPrice);
        } else if (maxPrice != null) {
            criteria = criteria.and("price").lessThanEqual(maxPrice);
        }
        // 分页
        Query query = new CriteriaQuery(criteria)
                .setPageable(PageRequest.of(current - 1, size));
        SearchHits<GoodsMapping> searchHits = elasticsearchTemplate.search(query, GoodsMapping.class);
        long totalValue = searchHits.getTotalHits();
        for (SearchHit<GoodsMapping> searchHit : searchHits.getSearchHits()) {
            GoodsMapping account = searchHit.getContent(); // 这就是得到的实体类
            GoodsVo convert = BeanUtil.toBean(account, GoodsVo.class);
            arrayList.add(convert);
        }
        return PageResponse.<GoodsVo>builder()
                .current(page.getCurrent())
                .size(page.getSize())
                .records(arrayList)
                .total(totalValue)
                .build();
    }

    @Override
    @Transactional
    public Result deleteGoods(Long goodsId) {
        TGoods goods = this.getById(goodsId);
        if(Objects.isNull(goods)){
            throw new ServiceException("商品为空");
        }else {
            objTagsService.deletePostTagById(goodsId,2);
            boolean removed = this.removeById(goodsId);
            if(removed){
                SpringUtils.context().publishEvent(new DeleteEvent(this,goodsId, "goods"));
            }
        }
        return Results.success();
    }

    @Override
    public TGoods findGoodsById(Long goodsId) {
        TGoods goods = this.getById(goodsId);
        if(ObjectUtil.isNull(goods)){
            throw new ServiceException("商品信息不存在");
        }
        return goods;
    }

}




