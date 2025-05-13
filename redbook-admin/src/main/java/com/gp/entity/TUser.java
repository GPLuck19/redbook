package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.Data;

/**
 * 用户表
 * @TableName t_user
 */
@TableName(value ="t_user")
@Data
public class TUser extends BaseDO {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 真实姓名
     */
    @TableField(value = "real_name")
    private String realName;

    /**
     * 国家/地区
     */
    @TableField(value = "region")
    private String region;

    /**
     * 证件类型
     */
    @TableField(value = "id_type")
    private Integer idType;

    /**
     * 证件号
     */
    @TableField(value = "id_card")
    private String idCard;

    /**
     * 手机号
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 固定电话
     */
    @TableField(value = "telephone")
    private String telephone;

    /**
     * 邮箱
     */
    @TableField(value = "mail")
    private String mail;

    /**
     * 头像
     */
    @TableField(value = "user_pic")
    private String userPic;

    /**
     * 审核状态
     */
    @TableField(value = "verify_status")
    private Integer verifyStatus;

    /**
     * 邮编
     */
    @TableField(value = "post_code")
    private String postCode;

    /**
     * 地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 注销时间戳
     */
    @TableField(value = "deletion_time")
    private Long deletionTime;

    /**
     * 背景图片
     */
    @TableField(value = "background")
    private String background;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}