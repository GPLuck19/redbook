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

package com.gp.controller.order;


import com.gp.dto.req.order.OrderListVo;
import com.gp.dto.req.order.OrderVo;
import com.gp.dto.resp.order.OrderList;
import com.gp.entity.TOperationLog;
import com.gp.entity.TOrder;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单服务控制层
 */
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final TOrderService orderService;


    /**
     * 创建订单
     */
    @PostMapping("/api/order-service/createOrder")
    public Result createOrder(@RequestBody OrderVo requestParam) {
        return Results.success(orderService.createOrder(requestParam));
    }


    /**
     * 查看订单列表
     */
    @PostMapping("/api/order-service/findOrderList")
    public Result<List<OrderList>> findOrderList(@RequestBody OrderListVo requestParam) {
        return Results.success(orderService.findOrderList(requestParam));
    }

    /**
     * 商户后台 =》查看订单列表
     */
    @PostMapping("/api/order-service/findAdminOrderList")
    public Result<PageResponse<OrderList>> findAdminOrderList(@RequestBody OrderListVo requestParam) {
        return Results.success(orderService.findAdminOrderList(requestParam));
    }


    /**
     * 取消订单
     */
    @PostMapping("/api/order-service/cancelOrder")
    public Result cancelOrder(@RequestParam(value = "orderSn") String orderSn) {
        return Results.success(orderService.cancelOrder(orderSn));
    }


    /**
     * 删除订单
     */
    @DeleteMapping("/api/order-service/deleteOrder")
    public Result deleteOrder(@RequestParam(value = "orderSn") String orderSn) {
        return Results.success(orderService.deleteOrder(orderSn));
    }



}
