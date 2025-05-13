package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.domian.LoginLogDao;
import com.gp.entity.TLoginLog;
import com.gp.page.PageResponse;

import java.util.Map;

/**
* @author Administrator
* @description 针对表【t_login_log(用户登录信息表)】的数据库操作Service
* @createDate 2024-12-16 10:11:23
*/
public interface TLoginLogService extends IService<TLoginLog> {

    PageResponse<TLoginLog> findLoginLogList(LoginLogDao requestParam);

    //使用redis优化查询
    Map findLoginLogListByRedis(LoginLogDao requestParam);


}
