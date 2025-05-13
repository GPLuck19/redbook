package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.Data;

/**
 * 权限表，存储系统中所有的权限信息
 * @TableName t_permissions
 */
@TableName(value ="t_permissions")
@Data
public class TPermissions extends BaseDO {
    /**
     * 权限唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 权限名称，例如“查看用户列表”
     */
    @TableField(value = "name")
    private String name;

    /**
     * 路由路径，对应前端的访问路径
     */
    @TableField(value = "route_path")
    private String routePath;

    /**
     * 前端组件路径，对应具体页面组件
     */
    @TableField(value = "component")
    private String component;

    /**
     * 父权限 ID，用于构建权限树
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 操作类型
     */
    @TableField(value = "operation_type")
    private Integer operationType;


    /**
     * 排序字段
     */
    @TableField(value = "sort_number")
    private Integer sortNumber;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}