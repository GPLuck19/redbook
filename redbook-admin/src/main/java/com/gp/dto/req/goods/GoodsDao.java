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

package com.gp.dto.req.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品保存请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsDao {

    /**
     * 商品id
     */
    private Long id;

    /**
     * 商品名称
     */
    private String goodsname;

    /**
     * 标签
     */
    private List<Long> tags;

    /**
     * 用户名
     */
    private String username;

    private Long userId;

    /**
     * 大分类
     */
    private String mt;

    /**
     * 大分类名称
     */
    private String mtname;

    /**
     * 小分类
     */
    private String st;

    /**
     * 小分类名称
     */
    private String stname;

    /**
     * 类型
     */
    private String grade;

    /**
     * 商品图片
     */
    private String pic;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 是否上架
     */
    private String ispub;

    /**
     * 新价格
     */
    private BigDecimal price;

    /**
     *  原价
     */
    private BigDecimal priceold;

    /**
     *  库存数量
     */
    private Long number;
}
