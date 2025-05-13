package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.dto.req.order.OrderListVo;
import com.gp.dto.req.order.OrderVo;
import com.gp.dto.resp.order.OrderDetails;
import com.gp.dto.resp.order.OrderList;
import com.gp.entity.TOrder;
import com.gp.page.PageResponse;
import com.gp.result.Result;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_order(订单表)】的数据库操作Service
* @createDate 2024-11-07 16:22:30
*/
public interface TOrderService extends IService<TOrder> {


    List<OrderList> findOrderList(OrderListVo requestParam);

    OrderDetails createOrder(OrderVo requestParam);

    Result cancelOrder(String orderSn);

    List<TOrder> findOrderListBySn(String orderSn);

    Result deleteOrder(String orderSn);

    PageResponse<OrderList> findAdminOrderList(OrderListVo requestParam);
}
