package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.Data;

/**
 * 好友关系表
 * @TableName t_user_friend
 */
@TableName(value ="t_user_friend")
@Data
public class TUserFriend extends BaseDO {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 好友ID
     */
    @TableField(value = "friend_id")
    private Long friendId;

    /**
     * 发起请求的用户的 ID
     */
    @TableField(value = "request_initiator")
    private Long requestInitiator;

    /**
     * 关系状态：0,好友申请 1-正常，2-黑名单
     */
    @TableField(value = "status")
    private Integer status;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}