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
 * 文章表
 * @TableName t_posts
 */
@TableName(value ="t_posts")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TPosts extends BaseDO {
    /**
     * 文章 ID
     */
    @TableId(value = "post_id", type = IdType.AUTO)
    private Long postId;

    /**
     * 作者用户 ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 文章标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 文章内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 文章配图路径（如果有）
     */
    @TableField(value = "image")
    private String image;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}