package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.Builder;
import lombok.Data;

/**
 * 用户角色关联表，管理用户与角色之间的多对多关系
 * @TableName t_user_roles
 */
@TableName(value ="t_user_roles")
@Data
@Builder
public class TUserRoles extends BaseDO {
    /**
     * 关联记录唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户 ID，关联用户表
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 角色 ID，关联角色表
     */
    @TableField(value = "role_id")
    private Long roleId;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}