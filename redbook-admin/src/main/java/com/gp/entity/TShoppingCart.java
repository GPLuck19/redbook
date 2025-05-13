package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车表
 * @TableName t_shopping_cart
 */
@TableName(value ="t_shopping_cart")
@Data
@Builder
public class TShoppingCart extends BaseDO {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    @TableField(value = "goodsId")
    private Long goodsId;

    /**
     * 
     */
    @TableField(value = "userId")
    private Long userId;


    @TableField(value = "pic")
    private String pic;

    @TableField(value = "description")
    private String description;

    @TableField(value = "price")
    private BigDecimal price;

    @TableField(value = "goodsname")
    private String goodsname;

    @TableField(value = "number")
    private Integer number;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}