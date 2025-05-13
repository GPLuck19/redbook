package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 对话表
 * @TableName t_ai_session
 */
@TableName(value ="t_ai_session")
@Data
@Builder
public class TAiSession {
    /**
     * 对话ID
     */
    @TableId(value = "session_id", type = IdType.AUTO)
    private Long sessionId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 会话名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private Date createdTime;
}