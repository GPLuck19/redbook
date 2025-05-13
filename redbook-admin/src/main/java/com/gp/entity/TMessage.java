package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.Builder;
import lombok.Data;

/**
 * 消息通知表
 * @TableName t_message
 */
@TableName(value ="t_message")
@Data
@Builder
public class TMessage extends BaseDO {
    /**
     * 消息ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
     * 发送者 ID
     */
    @TableField(value = "object_id")
    private Long objectId;

    /**
     * 消息内容
     */
    @TableField(value = "message")
    private String message;

    /**
     * 消息状态（未读、已读）
     */
    @TableField(value = "status")
    private Integer status;


    /**
     * 1.评论 2.点赞  3.转发
     */
    @TableField(value = "type")
    private Integer type;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}