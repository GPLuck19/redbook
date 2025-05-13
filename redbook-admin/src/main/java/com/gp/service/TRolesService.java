package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.dto.req.user.AssignRolesDao;
import com.gp.entity.TRoles;
import com.gp.result.Result;

/**
* @author Administrator
* @description 针对表【t_roles(角色表，存储系统中的角色信息)】的数据库操作Service
* @createDate 2024-12-03 10:58:33
*/
public interface TRolesService extends IService<TRoles> {

    Result assignRoles(AssignRolesDao requestParam);

    Result removeRole(Long id);
}
