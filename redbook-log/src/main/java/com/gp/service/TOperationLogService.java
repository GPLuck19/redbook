package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.domian.OperationLogDao;
import com.gp.entity.TOperationLog;
import com.gp.page.PageResponse;

import java.util.Map;

/**
* @author Administrator
* @description 针对表【t_operation_log(操作日志表)】的数据库操作Service
* @createDate 2024-12-16 10:11:18
*/
public interface TOperationLogService extends IService<TOperationLog> {


    PageResponse<TOperationLog> findOperaLogList(OperationLogDao requestParam);

    //使用redis优化查询
    Map findOperaLogListByRedis(OperationLogDao requestParam);

}
