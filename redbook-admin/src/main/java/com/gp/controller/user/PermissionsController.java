package com.gp.controller.user;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.gp.annotation.Log;
import com.gp.dto.resp.user.PermissionNode;
import com.gp.entity.TPermissions;
import com.gp.enums.OperatorType;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TPermissionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PermissionsController {

    private final TPermissionsService permissionsService;

    /**
     * 获取权限列表
     */
    @Log(title = "获取权限列表", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/permissions")
    @GetMapping("/api/user-service/permissions")
    public Result<List<PermissionNode>> permissions() {
        return Results.success(permissionsService.permissions());
    }

    /**
     * 删除权限
     *
     */
    @Log(title = "删除权限", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/removePermissions")
    @DeleteMapping("/api/user-service/removePermissions")
    public Result removePermissions(@RequestParam("id") Long id) {
        return Results.success(permissionsService.removePermissions(id));
    }

    /**
     * 新增权限信息
     */
    @Log(title = "新增权限信息", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/createPermission")
    @PostMapping("/api/user-service/createPermission")
    public Result createPermission(@RequestBody TPermissions requestParam) {
        return Results.success(permissionsService.createPermission(requestParam));
    }

}
