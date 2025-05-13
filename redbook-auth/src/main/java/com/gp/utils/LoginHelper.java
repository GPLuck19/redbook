package com.gp.utils;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.gp.userInfo.LoginUser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


/**
 * 登录鉴权助手
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginHelper {

    public static final String LOGIN_USER_KEY = "loginUser";
    public static final String USER_KEY = "userId";
    public static final String USER_NAME_KEY = "userName";

    public static final String CLIENT_KEY = "clientName";

    public static final String SYSTEM_KEY = "systemName";


    /**
     * 登录系统 基于 设备类型
     * 针对相同用户体系不同设备
     * @param loginUser 登录用户信息
     */
    public static void login(LoginUser loginUser) {
        SaLoginModel model = new SaLoginModel();
        StpUtil.login(loginUser.getLoginId(),
                model.setExtra(USER_KEY, loginUser.getUserId())
                        .setExtra(USER_NAME_KEY, loginUser.getUsername())
                        .setExtra(CLIENT_KEY, loginUser.getBrowser())
                        .setExtra(SYSTEM_KEY, loginUser.getOs())
        );
        StpUtil.getTokenSession().set(LOGIN_USER_KEY, loginUser);
    }

    /**
     * 获取用户(多级缓存)
     */
    public static LoginUser getLoginUser() {
        SaSession session = StpUtil.getTokenSession();
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        return (LoginUser) session.get(LOGIN_USER_KEY);
    }

    /**
     * 获取用户基于token
     */
    public static LoginUser getLoginUser(String token) {
        SaSession session = StpUtil.getTokenSessionByToken(token);
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        return (LoginUser) session.get(LOGIN_USER_KEY);
    }

    /**
     * 获取用户id
     */
    public static Long getUserId() {
        return Convert.toLong(getExtra(USER_KEY));
    }

    /**
     * 获取用户账户
     */
    public static String getUsername() {
        return Convert.toStr(getExtra(USER_NAME_KEY));
    }


    /**
     * 获取当前 Token 的扩展信息
     *
     * @param key 键值
     * @return 对应的扩展数据
     */
    private static Object getExtra(String key) {
        try {
            return StpUtil.getExtra(key);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 检查当前用户是否已登录
     *
     * @return 结果
     */
    public static boolean isLogin() {
        try {
            return getLoginUser() != null;
        } catch (Exception e) {
            return false;
        }
    }

}
