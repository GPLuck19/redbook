package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.dto.req.user.UserListDao;
import com.gp.dto.req.user.UserLockDao;
import com.gp.dto.req.user.UserRegisterDao;
import com.gp.dto.req.user.UserUpdateDao;
import com.gp.dto.resp.user.UserInfo;
import com.gp.dto.resp.user.UserRegisterVo;
import com.gp.entity.TUser;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.userInfo.SysUserOnline;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* @author Administrator
* @description 针对表【t_user(用户表)】的数据库操作Service
* @createDate 2024-10-12 15:00:15
*/
public interface TUserService extends IService<TUser> {

    UserInfo getUserInfoById(Long userId);

    Result updateAvatar(Long userId, String avatar);

    Result updateUserById(UserUpdateDao requestParam);

    UserRegisterVo register(UserRegisterDao requestParam);

    PageResponse<TUser> userList(UserListDao requestParam);

    Result lockUser(UserLockDao requestParam);

    Result kickout(Long userId);

    List<SysUserOnline> equipmentManagement();

    Map<Long, UserInfo> getUserInfoByIds(Set<Long> userIds);

    Result removeDevice(String tokenId);

    Result updateBackground(Long userId, String background);
}
