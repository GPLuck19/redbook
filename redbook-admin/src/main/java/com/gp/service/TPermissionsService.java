package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.dto.resp.user.PermissionNode;
import com.gp.entity.TPermissions;
import com.gp.result.Result;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_permissions(权限表，存储系统中所有的权限信息)】的数据库操作Service
* @createDate 2024-12-03 10:58:01
*/
public interface TPermissionsService extends IService<TPermissions> {

    public List<PermissionNode> getUserModules(Long userId);

    List<PermissionNode> permissions();

    Result removePermissions(Long id);

    Result createPermission(TPermissions requestParam);
}
