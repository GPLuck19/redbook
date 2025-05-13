package com.gp.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gp.entity.TGoods;
import com.gp.entity.TObjTags;
import com.gp.entity.TTags;
import com.gp.es.esMapping.GoodsMapping;
import com.gp.es.service.GoodsInfoRepository;
import com.gp.es.service.VideoInfoRepository;
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
public class DeleteActionListener {

    private final GoodsInfoRepository goodsInfoRepository;

    private final VideoInfoRepository videoInfoRepository;

    /**
     * 单条删除es中数据
     */
    @Async
    @EventListener
    public void doDelete(DeleteEvent event) {
        if(event.getEventType().equals("goods")){
            goodsInfoRepository.deleteById(String.valueOf(event.getTargetId()));
            log.info("商品数据在es中删除成功！");
        }else if (event.getEventType().equals("video")){
            videoInfoRepository.deleteById(String.valueOf(event.getTargetId()));
            log.info("视频数据在es中删除成功！");
        }
    }
}
