package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 对话消息表
 * @TableName t_ai_message
 */
@TableName(value ="t_ai_message")
@Data
@Builder
public class TAiMessage implements Serializable {
    /**
     * 对话ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    @TableField(value = "created_time")
    private Date createdTime;

    /**
     * 
     */
    @TableField(value = "creator_id")
    private Long creatorId;

    /**
     * 消息类型(用户/助手/系统)
     */
    @TableField(value = "type")
    private String type;

    /**
     * 消息内容
     */
    @TableField(value = "text_content")
    private String textContent;

    /**
     * 媒体内容如图片链接、语音链接
     */
    @TableField(value = "medias")
    private String medias;

    /**
     * 会话id
     */
    @TableField(value = "ai_session_id")
    private String aiSessionId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}