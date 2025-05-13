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



import com.gp.dto.req.goods.AddShoppingCart;
import com.gp.dto.req.goods.Goods;
import com.gp.entity.TShoppingCart;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商城商品服务控制层
 */
@RestController
@RequiredArgsConstructor
public class ShoppingCartController {

    private final TShoppingCartService shoppingCartService;

    /**
     * 商品购物车操作
     */
    @PostMapping("/api/goods-service/addShoppingCart")
    public Result addShoppingCart(@RequestBody AddShoppingCart requestParam) {
        return Results.success(shoppingCartService.addShoppingCart(requestParam));
    }

    /**
     * 移除商品购物车操作
     */
    @DeleteMapping("/api/goods-service/removeShoppingCart/{shopId}")
    public Result removeShoppingCart(@PathVariable("shopId") Long shopId) {
        return Results.success(shoppingCartService.removeShoppingCart(shopId));
    }


    /**
     * 减少商品购物车操作
     */
    @DeleteMapping("/api/goods-service/reductionShoppingCart/{shopId}")
    public Result reductionShoppingCart(@PathVariable("shopId") Long shopId) {
        return Results.success(shoppingCartService.reductionShoppingCart(shopId));
    }

    /**
     * 增加商品购物车操作
     */
    @PostMapping("/api/goods-service/plusShoppingCart/{shopId}")
    public Result plusShoppingCart(@PathVariable("shopId") Long shopId) {
        return Results.success(shoppingCartService.plusShoppingCart(shopId));
    }

    /**
     * 查看商品购物车列表
     */
    @PostMapping("/api/goods-service/findShoppingCartList")
    public Result<PageResponse<TShoppingCart>> findShoppingCartList(@RequestBody Goods requestParam) {
        return Results.success(shoppingCartService.findShoppingCartList(requestParam));
    }

}
