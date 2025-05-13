package com.gp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.entity.TUserRoles;
import com.gp.mapper.TUserRolesMapper;
import com.gp.service.TUserRolesService;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【t_user_roles(用户角色关联表，管理用户与角色之间的多对多关系)】的数据库操作Service实现
* @createDate 2024-12-03 10:58:43
*/
@Service
public class TUserRolesServiceImpl extends ServiceImpl<TUserRolesMapper, TUserRoles>
    implements TUserRolesService{

}




