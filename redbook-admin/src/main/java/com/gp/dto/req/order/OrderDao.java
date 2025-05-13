package com.gp.dto.req.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 购物车订单合并项
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDao {

    private Long goodsId;

    private BigDecimal price;

    private int quantity;

}