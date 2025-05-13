package com.gp.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gp.entity.TGoods;
import com.gp.entity.TObjTags;
import com.gp.entity.TTags;
import com.gp.es.esMapping.GoodsMapping;
import com.gp.es.service.GoodsInfoRepository;
import com.gp.listener.event.DeleteEvent;
import com.gp.mapper.TObjTagsMapper;
import com.gp.mapper.TTagsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Component
@Slf4j
public class GoodsActionListener {

    private final GoodsInfoRepository goodsInfoRepository;

    private final TObjTagsMapper objTagsMapper;

    private final TTagsMapper tagsMapper;


    /**
     * 商品发布入es库
     */
    @Async
    @EventListener
    public void doPubGoods(TGoods goods) {
        GoodsMapping goodsMapping = new GoodsMapping();
        goodsMapping.setId(goods.getId());
        goodsMapping.setGoodsname(goods.getGoodsname());
        LambdaQueryWrapper<TObjTags> wrapper = Wrappers.lambdaQuery(TObjTags.class).eq(TObjTags::getObjId, goods.getId()).eq(TObjTags::getType, 2);
        List<String> tagList = new ArrayList<>();
        List<TObjTags> tObjTags = objTagsMapper.selectList(wrapper);
        for (int i = 0; i < tObjTags.size(); i++) {
            TTags tags = tagsMapper.selectById(tObjTags.get(i).getTagId());
            tagList.add(String.valueOf(tags.getTagId()));
        }
        goodsMapping.setTags(tagList);
        goodsMapping.setUsername(goods.getUsername());
        goodsMapping.setUserId(goods.getUserId());
        goodsMapping.setMt(goods.getMt());
        goodsMapping.setMtname(goods.getMtname());
        goodsMapping.setSt(goods.getSt());
        goodsMapping.setStname(goods.getStname());
        goodsMapping.setGrade(goods.getGrade());
        goodsMapping.setPic(goods.getPic());
        goodsMapping.setDescription(goods.getDescription());
        goodsMapping.setIspub(goods.getIspub());
        goodsMapping.setPrice(goods.getPrice());
        goodsMapping.setPriceold(goods.getPriceold());
        goodsMapping.setNumber(goods.getNumber());
        goodsMapping.setCreateTime(goods.getCreateTime());
        goodsMapping.setUpdateTime(goods.getUpdateTime());
        goodsMapping.setNameSuggest(goods.getGoodsname());
        goodsInfoRepository.save(goodsMapping);
        log.info("商品数据插入es成功！");
    }
}
