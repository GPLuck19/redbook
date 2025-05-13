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

/**
 * 评论表
 * @TableName t_comments
 */
@TableName(value ="t_comments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TComments extends BaseDO {
    /**
     * 评论 ID
     */
    @TableId(value = "comment_id", type = IdType.AUTO)
    private Long commentId;


    /**
     * 父级评论 ID
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 文章 ID
     */
    @TableField(value = "post_id")
    private Long postId;

    /**
     * 评论用户 ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 评论内容
     */
    @TableField(value = "content")
    private String content;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}