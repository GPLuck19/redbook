package com.gp.service;

import cn.dev33.satoken.stp.StpInterface;
import com.gp.userInfo.LoginUser;
import com.gp.utils.LoginHelper;

import java.util.List;

/**
 * sa-token 权限管理实现类
 */
public class SaPermissionImpl implements StpInterface {

    /**
     * 获取菜单权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        return loginUser.getMenuPermission();

    }

    /**
     * 获取角色权限列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        return loginUser.getRoles();
    }


}
