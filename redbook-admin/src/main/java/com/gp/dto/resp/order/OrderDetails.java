package com.gp.dto.resp.order;

import com.gp.entity.TOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetails {

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 订单详情
     */
    private List<TOrder> OrderDetails;

}
