package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.gp.base.BaseDO;
import lombok.Builder;
import lombok.Data;

/**
 * 朋友圈动态表
 * @TableName t_moments
 */
@TableName(value ="t_moments")
@Data
@Builder
public class TMoments extends BaseDO {
    /**
     * 动态ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 动态内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 图片链接，多个用逗号分隔
     */
    @TableField(value = "images")
    private String images;

    /**
     * 地理位置
     */
    @TableField(value = "location")
    private String location;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}