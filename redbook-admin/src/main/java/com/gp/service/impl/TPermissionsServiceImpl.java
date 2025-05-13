package com.gp.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.dto.resp.user.PermissionNode;
import com.gp.entity.TPermissions;
import com.gp.entity.TRolePermissions;
import com.gp.mapper.TPermissionsMapper;
import com.gp.mapper.TRolePermissionsMapper;
import com.gp.mapper.TUserRolesMapper;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TPermissionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【t_permissions(权限表，存储系统中所有的权限信息)】的数据库操作Service实现
* @createDate 2024-12-03 10:58:01
*/
@Service
@RequiredArgsConstructor
public class TPermissionsServiceImpl extends ServiceImpl<TPermissionsMapper, TPermissions>
    implements TPermissionsService{

    private final TUserRolesMapper userRolesMapper;

    private final TRolePermissionsMapper rolePermissionsMapper;

    private final TPermissionsMapper permissionsMapper;

    /**
     * 获取用户的模块数据
     * @param userId 用户ID
     * @return 用户的模块列表
     */
    public List<PermissionNode> getUserModules(Long userId) {
        // 获取用户的角色ID列表
        List<Long> roleIds = userRolesMapper.getRoleIdsByUserId(userId);

        // 获取角色对应的权限ID列表
        List<Long> permissionIds = rolePermissionsMapper.getPermissionIdsByRoleIds(roleIds);

        // 查询权限列表
        List<TPermissions> permissions = permissionsMapper.getPermissionsByIds(permissionIds);

        // 构建权限树
        return buildPermissionTree(permissions);
    }

    @Override
    public List<PermissionNode> permissions() {
        LambdaQueryWrapper<TPermissions> queryWrapper = Wrappers.lambdaQuery(TPermissions.class).orderByDesc(TPermissions::getSortNumber);
        List<TPermissions> permissions = permissionsMapper.selectList(queryWrapper);
        // 构建权限树
        return buildPermissionTree(permissions);
    }

    @Override
    @Transactional
    public Result removePermissions(Long id) {
        permissionsMapper.deleteById(id);
        //删除角色和权限关联数据
        rolePermissionsMapper.delete(Wrappers.lambdaQuery(TRolePermissions.class).eq(TRolePermissions::getPermissionId,id));
        return Results.success();
    }

    @Override
    public Result createPermission(TPermissions requestParam) {
        if(ObjectUtil.isNotEmpty(requestParam.getId())){
            this.updateById(requestParam);
        }else {
            this.save(requestParam);
        }
        return null;
    }

    /**
     * 构建权限树
     * @param permissions 权限列表
     * @return 权限树形结构
     */
    private List<PermissionNode> buildPermissionTree(List<TPermissions> permissions) {
        // 创建一个映射，用于存储 ID 到节点的映射关系
        Map<Long, PermissionNode> nodeMap = new HashMap<>();
        List<PermissionNode> rootNodes = new ArrayList<>();

        // 第一遍：初始化所有节点并建立映射
        for (TPermissions permission : permissions) {
            PermissionNode node = new PermissionNode(permission);
            nodeMap.put(permission.getId(), node);

            // 如果是根节点，直接添加到根节点列表
            if (permission.getParentId() == null || permission.getParentId() == 0) {
                rootNodes.add(node);
            }
        }

        // 第二遍：将子节点挂载到父节点下
        for (TPermissions permission : permissions) {
            Long parentId = permission.getParentId();
            if (parentId != null && parentId != 0) {
                PermissionNode parent = nodeMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(nodeMap.get(permission.getId()));
                }
            }
        }

        return rootNodes;
    }

}






