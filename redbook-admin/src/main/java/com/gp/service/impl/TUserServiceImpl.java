package com.gp.service.impl;


import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.TokenSign;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SmUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.constant.UserConstant;
import com.gp.dto.req.user.UserListDao;
import com.gp.dto.req.user.UserLockDao;
import com.gp.dto.req.user.UserRegisterDao;
import com.gp.dto.req.user.UserUpdateDao;
import com.gp.dto.resp.user.FriendVo;
import com.gp.dto.resp.user.UserInfo;
import com.gp.dto.resp.user.UserRegisterVo;
import com.gp.entity.TUser;
import com.gp.exception.ClientException;
import com.gp.exception.ServiceException;
import com.gp.mapper.TUserMapper;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TUserFriendService;
import com.gp.service.TUserService;
import com.gp.userInfo.SysUserOnline;
import com.gp.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gp.common.constant.UserRedisKey.*;
import static com.gp.common.enums.UserRegisterErrorCodeEnum.*;

/**
* @author Administrator
* @description 针对表【t_user(用户表)】的数据库操作Service实现
* @createDate 2024-10-12 15:00:15
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser>
    implements TUserService{

    private final RedissonClient redissonClient;

    private final TUserMapper userMapper;

    private final TUserFriendService userFriendService;


    //密码校验器
    private String regex = "(?=.*[a-z])(?=.*[A-Z])(?=.*[~!@&%#_])[a-zA-Z0-9~!@&%#_]{8,16}";
    private Pattern pattern = Pattern.compile(regex);



    @Override
    public UserInfo getUserInfoById(Long userId) {
        TUser user = this.getById(userId);
        if (user == null) {
            throw new ClientException("用户不存在!");
        }
        //获取ip详细地址
        String operIp = IpUtils.getIpAddr();
        String ip = IpAddressUtils.getRegion(operIp);
        List<FriendVo> friendList = userFriendService.getFriendList(userId, "");
        UserInfo userInfo = BeanUtil.toBean(user, UserInfo.class);
        userInfo.setIp(ip);
        userInfo.setFriendsCount(friendList.size());
        return userInfo;
    }

    @Override
    public Result updateAvatar(Long userId, String avatar) {
        TUser user = this.getById(userId);
        if (user == null) {
            throw new ClientException("用户不存在!");
        }else {
            user.setUserPic(avatar);
            this.updateById(user);
            return Results.success();
        }
    }

    @Override
    public Result updateUserById(UserUpdateDao requestParam) {
        TUser user = this.getById(requestParam.getUserId());
        BeanUtil.copyProperties(requestParam,user);
        boolean update = this.updateById(user);
        if(update){
            return Results.success();
        }
        RedisUtils.deleteObject(UserConstant.USER_INFO_KEY+user.getId());
        throw new ServiceException(USER_UPDATE_FAIL);
    }

    @Override
    public UserRegisterVo register(UserRegisterDao requestParam) {
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER + requestParam.getUsername());
        try {
            if(lock.tryLock(REDIS_WAIT_TIME,
                    REDIS_LEASETIME,
                    TimeUnit.SECONDS)){
                    TUser user = BeanUtil.toBean(requestParam, TUser.class);
                    Matcher matcher = pattern.matcher(user.getPassword());
                    boolean matchStatus = matcher.matches();
                    if (!matchStatus) {
                        throw new ServiceException(PASSWORD_NOT);
                    }
                    String sm3Password = SmUtil.sm3(user.getPassword());
                    user.setPassword(sm3Password);
                    boolean save = this.save(user);
                    if (!save) {
                        throw new ServiceException(USER_REGISTER_FAIL);
                    }
            }else {
                throw new ServiceException(USER_REGISTER_FAIL);
            }
        }catch (Exception e){
            throw new ServiceException(USER_REGISTER_FAIL);
        }finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return BeanUtil.toBean(requestParam, UserRegisterVo.class);
    }


    @Override
    public Result lockUser(UserLockDao requestParam) {
        // 封禁指定账号
        /*StpUtil.disable(10001, 86400);*/
        // 封禁指定用户能力
        StpUtil.disable(requestParam.getLoginId(), requestParam.getPath(), requestParam.getLockTime());
        String message = "";
        switch (requestParam.getPath()) {
            case "addComment" -> message="你已经被禁止评论了！";
            case "addPost" -> message="你已经禁止发布文章了！";
            case "sendMessage" -> message="你已经被禁言了！";
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("receiverId", requestParam.getUserId());
        jsonObject.put("message", message);
        jsonObject.put("messageType", "PROHIBIT");
        SseMessageUtils.sendMessage(requestParam.getUserId(), jsonObject.toJSONString());
        return Results.success();
    }

    @Override
    public Result kickout(Long userId) {
        // 踢人下线不会清除Token信息，而是将其打上特定标记，再次访问会提示：Token已被踢下线。
        StpUtil.kickout(userId);
        //发送通知
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("receiverId", userId);
        jsonObject.put("message", "你已经被强制下线了！");
        jsonObject.put("messageType", "WARN");
        SseMessageUtils.sendMessage(userId, jsonObject.toJSONString());
        return Results.success();
    }

    @Override
    public Result updateBackground(Long userId, String background) {
        TUser user = this.getById(userId);
        if (user == null) {
            throw new ClientException("用户不存在!");
        }else {
            user.setBackground(background);
            this.updateById(user);
            return Results.success();
        }
    }

    @Override
    public Result removeDevice(String tokenId) {
        StpUtil.logoutByTokenValue(tokenId);
        return Results.success();
    }

    @Override
    public Map<Long, UserInfo> getUserInfoByIds(Set<Long> userIds) {
        Map<Long, UserInfo> userInfoMap = new HashMap<>();

        // 先从缓存中获取
        for (Long userId : userIds) {
            String cacheKey = UserConstant.USER_INFO_KEY + userId;
            UserInfo cachedUserInfo = RedisUtils.getCacheObject(cacheKey);
            if (cachedUserInfo != null) {
                userInfoMap.put(userId, cachedUserInfo);
            }
        }

        // 获取缓存未命中的用户 ID
        Set<Long> missedUserIds = new HashSet<>(userIds);
        missedUserIds.removeAll(userInfoMap.keySet());

        // 批量查询数据库，填充未命中的数据
        if (!missedUserIds.isEmpty()) {
            List<UserInfo> userInfoVos = userMapper.selectUserInfoByIds(missedUserIds);
            for (UserInfo userInfo : userInfoVos) {
                userInfoMap.put(userInfo.getId(), userInfo);
                // 缓存用户信息
                RedisUtils.setCacheObject(UserConstant.USER_INFO_KEY + userInfo.getId(), userInfo, Duration.ofMinutes(3600)); // 缓存 1 天
            }
        }
        return userInfoMap;
    }

    @Override
    public List<SysUserOnline> equipmentManagement() {
        SaSession session = StpUtil.getSession();
        List<TokenSign> tokenSignList = session.getTokenSignList();
        List<SysUserOnline> sysList = new ArrayList<>();
        tokenSignList.stream().forEach( tokenSign -> {
            String tokenSignValue = tokenSign.getValue();
            SysUserOnline userOnline  = RedisUtils.getCacheObject(UserConstant.ONLINE_TOKEN_KEY + tokenSignValue);
            sysList.add(userOnline);
        });
        System.out.println("会话id：" + session.getId() + "，共在 " + tokenSignList.size() + " 设备登录");
        return sysList;
    }

    @Override
    public PageResponse<TUser> userList(UserListDao requestParam) {
        LambdaQueryWrapper<TUser> queryWrapper = Wrappers.lambdaQuery(TUser.class)
                .orderByDesc(TUser::getCreateTime);
        if(ObjectUtil.isNotEmpty(requestParam.getUsername())){
            queryWrapper.like(TUser::getUsername, requestParam.getUsername());
        }
        IPage<TUser> userPage = userMapper.selectPage(PageUtil.convert(requestParam), queryWrapper);
        return PageResponse.<TUser>builder()
                .current(userPage.getCurrent())
                .size(userPage.getSize())
                .records(userPage.getRecords())
                .total(userPage.getTotal())
                .build();
    }
}




