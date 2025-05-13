package com.gp.log;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.gp.constant.Constants;
import com.gp.constant.GlobalConstants;
import com.gp.entity.TLoginLog;
import com.gp.event.LoginLogEvent;
import com.gp.mapper.TLoginLogMapper;
import com.gp.utils.IpAddressUtils;
import com.gp.utils.RedisUtils;
import com.gp.utils.ServletUtils;
import com.gp.utils.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * 系统访问日志情况信息 服务层处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogService {

    private final TLoginLogMapper loginLogMapper;


    /**
     * 记录登录信息
     *
     * @param loginLogEvent 登录事件
     */
    @Async
    @EventListener
    public void LoginLog(LoginLogEvent loginLogEvent) {
        HttpServletRequest request = loginLogEvent.getRequest();
        final UserAgent userAgent = UserAgentUtil.parse(request.getHeader("User-Agent"));
        final String ip = ServletUtils.getClientIP(request);
        String address = IpAddressUtils.getRegion(ip);
        StringBuilder s = new StringBuilder();
        s.append(getBlock(ip));
        s.append(address);
        s.append(getBlock(loginLogEvent.getUsername()));
        s.append(getBlock(loginLogEvent.getStatus()));
        s.append(getBlock(loginLogEvent.getMessage()));
        // 获取客户端操作系统
        String os = userAgent.getOs().getName();
        // 获取客户端浏览器
        String browser = userAgent.getBrowser().getName();
        // 封装对象
        TLoginLog logininfor = new TLoginLog();
        logininfor.setUserName(loginLogEvent.getUsername());
        logininfor.setIpaddr(ip);
        logininfor.setLoginLocation(address);
        logininfor.setBrowser(browser);
        logininfor.setOs(os);
        logininfor.setMsg(loginLogEvent.getMessage());
        logininfor.setLoginTime(new Date());
        // 日志状态
        if (StringUtils.equalsAny(loginLogEvent.getStatus(), Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
            logininfor.setStatus(Integer.valueOf(Constants.SUCCESS));
        } else if (Constants.LOGIN_FAIL.equals(loginLogEvent.getStatus())) {
            logininfor.setStatus(Integer.valueOf(Constants.FAIL));
        }
        // 插入数据
        loginLogMapper.insert(logininfor);
        log.info("登录日志插入成功！{}",logininfor);
    }

    private String getBlock(Object msg) {
        if (msg == null) {
            msg = "";
        }
        return "[" + msg.toString() + "]";
    }
}
