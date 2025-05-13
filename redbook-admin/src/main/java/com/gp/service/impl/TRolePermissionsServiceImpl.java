package com.gp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.dto.req.user.AssignPermissionsDao;
import com.gp.entity.TRolePermissions;
import com.gp.mapper.TRolePermissionsMapper;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TRolePermissionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【t_role_permissions(角色权限关联表，用于管理角色与权限之间的关系)】的数据库操作Service实现
* @createDate 2024-12-03 10:58:20
*/
@Service
@RequiredArgsConstructor
public class TRolePermissionsServiceImpl extends ServiceImpl<TRolePermissionsMapper, TRolePermissions>
    implements TRolePermissionsService{

    private final TRolePermissionsMapper rolePermissionsMapper;

    @Override
    public List<Long> rolePermissions(Long id) {
        return rolePermissionsMapper.findPermissionIdsByRoleId(id);
    }

    @Override
    @Transactional
    public Result removePermissionsById(Long roleId, List<Long> permissionIds) {
        rolePermissionsMapper.deleteByRoleIdAndPermissionIds(roleId, permissionIds);
        return Results.success();
    }

    @Override
    @Transactional
    public Result assignPermissions(AssignPermissionsDao requestParam) {
        // 先删除旧的权限
        rolePermissionsMapper.deleteByRoleId(requestParam.getRoleId());

        // 插入新的权限
        List<TRolePermissions> rolePermissions = requestParam.getPermissionIds().stream()
                .map(permissionId -> TRolePermissions.builder().roleId(requestParam.getRoleId()).permissionId(permissionId).build())
                .collect(Collectors.toList());
        rolePermissionsMapper.batchInsert(rolePermissions);
        return Results.success();
    }
}




