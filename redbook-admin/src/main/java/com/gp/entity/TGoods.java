package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品表
 * @TableName tb_goods
 */
@TableName(value ="t_goods")
@Data
public class TGoods extends BaseDO {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    @TableField(value = "goodsname")
    private String goodsname;

    /**
     * 商户名称
     */
    @TableField(value = "username")
    private String username;

    /**
     * 用户id
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 大分类
     */
    @TableField(value = "mt")
    private String mt;

    /**
     * 大分类名称
     */
    @TableField(value = "mtName")
    private String mtname;

    /**
     * 小分类
     */
    @TableField(value = "st")
    private String st;

    /**
     * 小分类名称
     */
    @TableField(value = "stName")
    private String stname;

    /**
     * 类型
     */
    @TableField(value = "grade")
    private String grade;

    /**
     * 商品图片
     */
    @TableField(value = "pic")
    private String pic;

    /**
     * 商品描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 是否上架
     */
    @TableField(value = "isPub")
    private Integer ispub;

    /**
     * 新价格
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     *  老价格
     */
    @TableField(value = "priceOld")
    private BigDecimal priceold;

    /**
     *  库存数量
     */
    @TableField(value = "number")
    private Long number;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}