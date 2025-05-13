package com.gp.controller.user;


import cn.dev33.satoken.annotation.SaCheckPermission;
import com.gp.annotation.Log;
import com.gp.dto.req.user.AssignPermissionsDao;
import com.gp.dto.req.user.AssignRolesDao;
import com.gp.entity.TRoles;
import com.gp.enums.OperatorType;
import com.gp.mapper.TUserRolesMapper;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TRolePermissionsService;
import com.gp.service.TRolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
/*@SaIgnore*/
public class RoleController {


    private final TRolesService rolesService;

    private final TRolePermissionsService rolePermissionsService;

    private final TUserRolesMapper userRolesMapper;



    /**
     * 获取角色列表
     */
    @Log(title = "获取角色列表", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/roles")
    @GetMapping("/api/user-service/roles")
    public Result<List<TRoles>> roles() {
        return Results.success(rolesService.list());
    }


    /**
     * 分配用户角色信息
     */
    @Log(title = "分配用户角色信息", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/assignRoles")
    @PostMapping("/api/user-service/assignRoles")
    public Result assignRoles(@RequestBody AssignRolesDao requestParam) {
        return Results.success(rolesService.assignRoles(requestParam));
    }


    /**
     * 给角色分配权限
     */
    @Log(title = "给角色分配权限", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/assignPermissions")
    @PostMapping("/api/user-service/assignPermissions")
    public Result assignPermissions(@RequestBody AssignPermissionsDao requestParam) {
        return Results.success(rolePermissionsService.assignPermissions(requestParam));
    }


    /**
     * 删除角色
     */
    @Log(title = "删除角色", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/removeRole")
    @DeleteMapping("/api/user-service/removeRole")
    public Result removeRole(@RequestParam("id") Long id) {
        return Results.success(rolesService.removeRole(id));
    }


    /**
     * 新增角色信息
     */
    @Log(title = "新增角色信息", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/createRole")
    @PostMapping("/api/user-service/createRole")
    public Result createRole(@RequestBody TRoles requestParam) {
        return Results.success(rolesService.save(requestParam));
    }


    /**
     * 查询角色的权限
     */
    @SaCheckPermission("/user/rolePermissions")
    @GetMapping("/api/user-service/rolePermissions")
    public Result<List<Long>> rolePermissions(@RequestParam("id") Long id) {
        return Results.success(rolePermissionsService.rolePermissions(id));
    }


    /**
     * 查询用户的权角色
     */
    @SaCheckPermission("/user/userRoles")
    @GetMapping("/api/user-service/userRoles")
    public Result<List<Long>> userRoles(@RequestParam("id") Long id) {
        return Results.success(userRolesMapper.getRoleIdsByUserId(id));
    }

    /**
     * 移除角色的权限
     */
    @Log(title = "移除角色的权限", operatorType = OperatorType.MANAGE)
    @DeleteMapping("/api/user-service/removePermissionsById")
    @SaCheckPermission("/user/removePermissionsById")
    public Result removePermissionsById(@RequestParam Long roleId,
                                                    @RequestBody List<Long> permissionIds) {
        return Results.success(rolePermissionsService.removePermissionsById(roleId, permissionIds));
    }
}
