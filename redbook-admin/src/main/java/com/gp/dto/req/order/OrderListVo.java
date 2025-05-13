package com.gp.dto.req.order;

import com.gp.page.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 
 * @TableName U_Order_1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderListVo extends PageRequest {

    private Long userId;


    /**
     * 商户 id
     */
    private Long sourceId;


    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 操作开始时间
     */
    private Date startTime;


    /**
     * 操作结束时间
     */
    private Date stopTime;

    private Integer status;
}