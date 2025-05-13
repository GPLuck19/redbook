package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.Data;

/**
 * 角色表，存储系统中的角色信息
 * @TableName t_roles
 */
@TableName(value ="t_roles")
@Data
public class TRoles extends BaseDO {
    /**
     * 角色唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称，例如“管理员”或“普通用户”
     */
    @TableField(value = "role_name")
    private String roleName;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}