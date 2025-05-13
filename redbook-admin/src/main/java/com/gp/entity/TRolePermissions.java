package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.Builder;
import lombok.Data;

/**
 * 角色权限关联表，用于管理角色与权限之间的关系
 * @TableName t_role_permissions
 */
@TableName(value ="t_role_permissions")
@Data
@Builder
public class TRolePermissions extends BaseDO {
    /**
     * 关联记录唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色 ID，关联角色表
     */
    @TableField(value = "role_id")
    private Long roleId;

    /**
     * 权限 ID，关联权限表
     */
    @TableField(value = "permission_id")
    private Long permissionId;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}