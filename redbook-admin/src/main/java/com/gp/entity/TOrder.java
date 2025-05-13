package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单表
 * @TableName t_order
 */
@TableName(value ="t_order")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TOrder extends BaseDO {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 主订单号
     */
    @TableField(value = "parent_order_sn")
    private String POrderSn;

    /**
     * 子订单号
     */
    @TableField(value = "order_sn")
    private String orderSn;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 商品ID
     */
    @TableField(value = "goods_id")
    private Long goodsId;

    /**
     * 商品名称
     */
    @TableField(value = "goods_name")
    private String goodsName;

    /**
     * 订单来源-商户 id
     */
    @TableField(value = "source_id")
    private Long sourceId;

    /**
     * 订单状态 1待支付  2.已取消  3.已支付
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 下单时间
     */
    @TableField(value = "order_time")
    private Date orderTime;

    /**
     * 支付方式
     */
    @TableField(value = "pay_type")
    private Integer payType;

    /**
     * 支付时间
     */
    @TableField(value = "pay_time")
    private Date payTime;

    /**
     * 订单金额
     */
    @TableField(value = "price")
    private BigDecimal price;


    /**
     * 下单地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 商品数量
     */
    @TableField(value = "number")
    private Integer number;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}