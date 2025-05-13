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

package com.gp.handler.filter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.gp.dto.req.order.OrderDao;
import com.gp.entity.TGoods;
import com.gp.exception.ClientException;
import com.gp.handler.GoodsQueryChainFilter;
import com.gp.mapper.TGoodsMapper;
import com.gp.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.gp.common.constant.GoodsRedisKey.GOODS_LIST_KEY;
import static com.gp.common.constant.GoodsRedisKey.GOODS_LOCK_LIST_KEY;


/**
 * 查询商品流程过滤器之验证数据是否正确
 */
@Component
@RequiredArgsConstructor
public class GoodsQueryParamVerifyChainFilter implements GoodsQueryChainFilter<OrderDao> {


    private final TGoodsMapper goodsMapper;

    /**
     * 缓存数据为空并且已经加载过标识
     */
    private static boolean CACHE_DATA_ISNULL_AND_LOAD_FLAG = false;

    @Override
    public void handler(OrderDao requestParam) {
        // 从 RedisUtils 中读取缓存
        List<Object> actualExistList = RedisUtils.multiGetFromRedisson(
                GOODS_LIST_KEY,
                Collections.singletonList(requestParam.getGoodsId().toString())
        );

        // 检查缓存为空的条件
        long emptyCount = actualExistList.stream().filter(Objects::isNull).count();

        if(emptyCount==0L){
            return;
        }

        if ((emptyCount != 0L && CACHE_DATA_ISNULL_AND_LOAD_FLAG) ||
                (emptyCount == 1L && CACHE_DATA_ISNULL_AND_LOAD_FLAG && RedisUtils.hasKey(GOODS_LIST_KEY))) {
            throw new ClientException("["+requestParam.getGoodsId() + "] => 商品暂无库存");
        }

        // 使用 Redisson 锁
        RLock lock = RedisUtils.getClient().getLock(GOODS_LOCK_LIST_KEY);
        lock.lock();
        try {
            // 数据库查询
            LambdaQueryWrapper<TGoods> wrappers = Wrappers.lambdaQuery(TGoods.class)
                    .eq(TGoods::getIspub,1)
                    .ge(TGoods::getNumber, 0);
            List<TGoods> regionDOList = goodsMapper.selectList(wrappers);

            // 构建缓存数据并存入 Redis
            Map<String, Object> regionValueMap = regionDOList.stream()
                    .collect(Collectors.toMap(
                            goods -> goods.getId().toString(),
                            TGoods::getGoodsname
                    ));
            RedisUtils.setCacheMap(GOODS_LIST_KEY, regionValueMap);

            // 设置标志位
            CACHE_DATA_ISNULL_AND_LOAD_FLAG = true;

            // 再次检查目标商品是否存在
            emptyCount = regionValueMap.keySet().stream()
                    .filter(id -> id.equals(requestParam.getGoodsId().toString()))
                    .count();
            if (emptyCount == 0L) {
                throw new ClientException("["+requestParam.getGoodsId() + "] => 商品暂无库存");
            }
        } finally {
            lock.unlock();
        }
    }


    @Override
    public int getOrder() {
        return 10;
    }
}
