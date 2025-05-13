package com.gp.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.dto.req.user.AssignRolesDao;
import com.gp.entity.TRolePermissions;
import com.gp.entity.TRoles;
import com.gp.entity.TUserRoles;
import com.gp.mapper.TRolePermissionsMapper;
import com.gp.mapper.TRolesMapper;
import com.gp.mapper.TUserRolesMapper;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TRolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【t_roles(角色表，存储系统中的角色信息)】的数据库操作Service实现
* @createDate 2024-12-03 10:58:33
*/
@Service
@RequiredArgsConstructor
public class TRolesServiceImpl extends ServiceImpl<TRolesMapper, TRoles>
    implements TRolesService{

    private final TUserRolesMapper userRolesMapper;

    private final TRolePermissionsMapper rolePermissionsMapper;

    @Override
    @Transactional
    public Result assignRoles(AssignRolesDao requestParam) {
        // 1. 删除用户之前的角色关联
        userRolesMapper.deleteRolesByUserId(requestParam.getUserId());
        // 2. 插入新的角色关联
        if (ObjectUtil.isNotEmpty(requestParam.getRoleIds())) {
            List<TUserRoles> userRoles = requestParam.getRoleIds().stream()
                    .map(roleId -> TUserRoles.builder().userId(requestParam.getUserId()).roleId(roleId).build())
                    .collect(Collectors.toList());
            userRolesMapper.insertUserRoles(userRoles);
        }
        return Results.success();
    }

    @Override
    public Result removeRole(Long id) {
        this.removeById(id);
        userRolesMapper.delete(Wrappers.lambdaQuery(TUserRoles.class).eq(TUserRoles::getRoleId,id));
        rolePermissionsMapper.delete(Wrappers.lambdaQuery(TRolePermissions.class).eq(TRolePermissions::getRoleId,id));
        return Results.success();
    }
}




