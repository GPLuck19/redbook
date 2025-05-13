package com.gp.dto.resp.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.gp.entity.TOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderList {

    /**
     * 订单流水号
     */
    private String orderSn;

    /**
     * 商品合并名称
     */
    private String subject;


    /**
     * 下单时间
     */
    private Date orderTime;

    /**
     * 订单状态 1待支付  2.已取消  3.已支付
     */
    private Integer status;


    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;


    /**
     * 下单地址
     */
    private String address;

    /**
     * 商品数量
     */
    private Integer number;

}
