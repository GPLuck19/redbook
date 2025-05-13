package com.gp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.constant.GlobalConstants;
import com.gp.domian.LoginLogDao;
import com.gp.entity.TLoginLog;
import com.gp.mapper.TLoginLogMapper;
import com.gp.page.PageResponse;
import com.gp.service.TLoginLogService;
import com.gp.utils.PageUtil;
import com.gp.utils.RedisUtils;
import com.gp.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【t_login_log(用户登录信息表)】的数据库操作Service实现
* @createDate 2024-12-16 10:11:23
*/
@Service
@RequiredArgsConstructor
public class TLoginLogServiceImpl extends ServiceImpl<TLoginLogMapper, TLoginLog>
    implements TLoginLogService{

    private final TLoginLogMapper loginLogMapper;

    @Override
    public PageResponse<TLoginLog> findLoginLogList(LoginLogDao requestParam) {
        LambdaQueryWrapper<TLoginLog> queryWrapper = Wrappers.lambdaQuery(TLoginLog.class)
                .eq(StringUtils.isNotBlank(requestParam.getUserName()), TLoginLog::getUserName, requestParam.getUserName())
                .orderByDesc(TLoginLog::getCreateTime);
        IPage<TLoginLog> loginPage = loginLogMapper.selectPage(PageUtil.convert(requestParam), queryWrapper);
        return PageResponse.<TLoginLog>builder()
                .current(loginPage.getCurrent())
                .size(loginPage.getSize())
                .records(loginPage.getRecords())
                .total(loginPage.getTotal())
                .build();
    }

    @Override
    public Map findLoginLogListByRedis(LoginLogDao requestParam) {
        Page page = PageUtil.convert(requestParam);
        int current =(int) page.getCurrent();
        int size = (int) page.getSize();
        List<String> logs = RedisUtils.getListByPage(GlobalConstants.LOGIN_LOG_KEY, current, size);
        long count = RedisUtils.getListSize(GlobalConstants.LOGIN_LOG_KEY);
        Map map = new HashMap();
        map.put("data", logs);
        map.put("count", count);
        return map;
    }
}




