package com.gp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.dto.resp.user.FriendVo;
import com.gp.entity.TUserFriend;
import com.gp.entity.TUserMessage;
import com.gp.exception.ServiceException;
import com.gp.mapper.TUserFriendMapper;
import com.gp.mapper.TUserMessageMapper;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TUserFriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_user_friend(好友关系表)】的数据库操作Service实现
* @createDate 2024-11-29 09:24:21
*/
@Service
@RequiredArgsConstructor
public class TUserFriendServiceImpl extends ServiceImpl<TUserFriendMapper, TUserFriend>
    implements TUserFriendService{

    private final TUserFriendMapper userFriendMapper;

    private final TUserMessageMapper userMessageMapper;

    @Override
    @Transactional
    public Result addFrined(Long userId, Long friendId) {
        // 检查是否已有关系记录
        QueryWrapper<TUserFriend> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("friend_id", friendId);
        if (userFriendMapper.selectCount(wrapper) > 0) {
            throw new ServiceException("已经发送过申请或已经是好友");// 已经发送过申请或已经是好友
        }
        // 插入申请记录
        TUserFriend userFriend = new TUserFriend();
        userFriend.setUserId(userId);
        userFriend.setFriendId(friendId);
        userFriend.setStatus(0); // 待处理状态
        userFriend.setRequestInitiator(userId);
        userFriendMapper.insert(userFriend);

        // 逆向记录
        TUserFriend reverseFriend = new TUserFriend();
        reverseFriend.setUserId(friendId);
        reverseFriend.setFriendId(userId);
        reverseFriend.setStatus(0); // 待处理状态
        reverseFriend.setRequestInitiator(userId);
        userFriendMapper.insert(reverseFriend);
        return Results.success();
    }

    @Override
    @Transactional
    public Result acceptFriend(Long userId, Long friendId) {
        // 更新状态为好友
        UpdateWrapper<TUserFriend> wrapper = new UpdateWrapper<>();
        wrapper.set("status", 1).eq("user_id", userId).eq("friend_id", friendId);
        userFriendMapper.update(null, wrapper);

        // 逆向记录也更新
        UpdateWrapper<TUserFriend> reverseWrapper = new UpdateWrapper<>();
        reverseWrapper.set("status", 1).eq("user_id", friendId).eq("friend_id", userId);
        userFriendMapper.update(null, reverseWrapper);

        //插入聊天记录去私信窗口
        TUserMessage message = TUserMessage.builder().content("你好").senderId(friendId).receiverId(userId).messageType(1).status(0).build();
        userMessageMapper.insert(message);
        return Results.success();
    }

    @Override
    public Boolean isFriend(Long userId, Long friendId) {
        // 检查好友关系是否有效
        QueryWrapper<TUserFriend> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("friend_id", friendId).eq("status", 1);
        if (userFriendMapper.selectCount(wrapper) == 0) {
            return false;
        }
        return true;
    }

    @Override
    public Result removeFriend(Long userId, Long friendId) {
        // 删除两条关系记录
        QueryWrapper<TUserFriend> wrapper = new QueryWrapper<>();
        wrapper.and(w -> w.eq("user_id", userId).eq("friend_id", friendId))
                .or(w -> w.eq("user_id", friendId).eq("friend_id", userId))
                .eq("status", 1); // 仅删除正常好友关系
        int rows = userFriendMapper.delete(wrapper);
        if(rows > 0){
            return Results.success();
        }
        throw new ServiceException("删除好友失败");
    }

    @Override
    public List<FriendVo> getFriendList(Long userId, String search) {
        return userFriendMapper.getFriendList(userId,1, search);
    }

    @Override
    public Result refuseFriend(Long userId, Long friendId) {
        // 删除两条关系记录
        QueryWrapper<TUserFriend> wrapper = new QueryWrapper<>();
        wrapper.and(w -> w.eq("user_id", userId).eq("friend_id", friendId)
                        .or()
                        .eq("user_id", friendId).eq("friend_id", userId))
                .eq("status", 0); // 确保 status=0 应用于整个条件
        int rows = userFriendMapper.delete(wrapper);
        if(rows > 0){
            return Results.success();
        }
        throw new ServiceException("拒绝好友失败");
    }

    @Override
    public List<FriendVo> getPendingFriends(Long userId, String search) {
        return userFriendMapper.PendingFriends(userId,0, search);
    }
}




