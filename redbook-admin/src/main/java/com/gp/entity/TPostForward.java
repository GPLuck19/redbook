package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.Builder;
import lombok.Data;

/**
 * 文章转发文章
 * @TableName t_post_forward
 */
@TableName(value ="t_post_forward")
@Data
@Builder
public class TPostForward extends BaseDO {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文章 ID
     */
    @TableField(value = "post_id")
    private Long postId;

    /**
     * 接收者 ID
     */
    @TableField(value = "receiver_id")
    private Long receiverId;

    /**
     * 发送者 ID
     */
    @TableField(value = "push_id")
    private Long pushId;

    /**
     * 文章标题
     */
    @TableField(value = "title")
    private String title;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}