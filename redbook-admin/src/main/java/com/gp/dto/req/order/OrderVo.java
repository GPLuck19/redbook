package com.gp.dto.req.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 
 * @TableName U_Order_1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderVo {

    /**
     * 总价
     */
    private BigDecimal totalAmount;

    /**
     * 地址id
     */
    private Long addressId;

    /**
     * 用户id
     */
    private Long userId;

    private List<OrderDao> goodsList;



}