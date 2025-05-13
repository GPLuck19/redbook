package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.dto.req.user.AssignPermissionsDao;
import com.gp.entity.TRolePermissions;
import com.gp.result.Result;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_role_permissions(角色权限关联表，用于管理角色与权限之间的关系)】的数据库操作Service
* @createDate 2024-12-03 10:58:20
*/
public interface TRolePermissionsService extends IService<TRolePermissions> {

    List<Long> rolePermissions(Long id);

    Result removePermissionsById(Long roleId, List<Long> permissionIds);

    Result assignPermissions(AssignPermissionsDao requestParam);
}
