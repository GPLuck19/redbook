package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.dto.resp.user.FriendVo;
import com.gp.entity.TUserFriend;
import com.gp.result.Result;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_user_friend(好友关系表)】的数据库操作Service
* @createDate 2024-11-29 09:24:21
*/
public interface TUserFriendService extends IService<TUserFriend> {

    Result addFrined(Long userId, Long friendId);

    Result acceptFriend(Long userId, Long friendId);

    Boolean isFriend(Long userId, Long friendId);

    Result removeFriend(Long userId, Long friendId);

    List<FriendVo> getFriendList(Long userId, String search);

    List<FriendVo> getPendingFriends(Long userId, String search);

    Result refuseFriend(Long userId, Long friendId);
}
