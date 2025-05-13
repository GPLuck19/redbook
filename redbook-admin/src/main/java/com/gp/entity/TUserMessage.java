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
 * 用户私信表
 * @TableName t_user_message
 */
@TableName(value ="t_user_message")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TUserMessage extends BaseDO {
    /**
     * 消息ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发送者ID
     */
    @TableField(value = "sender_id")
    private Long senderId;

    /**
     * 接收者ID
     */
    @TableField(value = "receiver_id")
    private Long receiverId;

    /**
     * 消息内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 状态：0-未读，1-已读
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 消息类型：1-正常消息，2-转发文章消息，3图片信息
     */
    @TableField(value = "messageType")
    private Integer messageType;

    /**
     * 原始消息id
     */
    @TableField(value = "originalMessageId")
    private Long originalMessageId;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}