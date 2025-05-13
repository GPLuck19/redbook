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
 * 朋友圈评论表
 * @TableName t_moment_comments
 */
@TableName(value ="t_moment_comments")
@Data
@Builder
public class TMomentComments extends BaseDO {
    /**
     * 评论ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 动态ID
     */
    @TableField(value = "moment_id")
    private Long momentId;

    /**
     * 评论的用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 评论内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 父级评论 ID
     */
    @TableField(value = "parent_id")
    private Long parentId;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}