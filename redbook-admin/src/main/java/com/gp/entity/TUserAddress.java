package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.Data;

/**
 * 
 * @TableName t_user_address
 */
@TableName(value ="t_user_address")
@Data
public class TUserAddress extends BaseDO {
    /**
     * 地址记录的唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户 ID，关联用户表，确保地址属于特定用户
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 地址标签，用于用户标记地址用途，如“家”“公司”等
     */
    @TableField(value = "address_label")
    private String addressLabel;

    /**
     * 详细地址信息，包含街道名称、门牌号等
     */
    @TableField(value = "address_detail")
    private String addressDetail;

    /**
     * 邮编
     */
    @TableField(value = "zip_code")
    private String zipCode;

    /**
     * 城市
     */
    @TableField(value = "city")
    private String city;

    /**
     * 省份
     */
    @TableField(value = "province")
    private String province;

    /**
     * 收货人姓名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 电话号码
     */
    @TableField(value = "phone")
    private Long phone;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}