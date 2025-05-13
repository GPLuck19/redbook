package com.gp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.constant.GlobalConstants;
import com.gp.domian.OperationLogDao;
import com.gp.entity.TOperationLog;
import com.gp.mapper.TOperationLogMapper;
import com.gp.page.PageResponse;
import com.gp.service.TOperationLogService;
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
* @description 针对表【t_operation_log(操作日志表)】的数据库操作Service实现
* @createDate 2024-12-16 10:11:18
*/
@Service
@RequiredArgsConstructor
public class TOperationLogServiceImpl extends ServiceImpl<TOperationLogMapper, TOperationLog>
    implements TOperationLogService{

    private final TOperationLogMapper operationLogMapper;

    @Override
    public PageResponse<TOperationLog> findOperaLogList(OperationLogDao requestParam) {
        LambdaQueryWrapper<TOperationLog> queryWrapper = Wrappers.lambdaQuery(TOperationLog.class)
                .orderByDesc(TOperationLog::getCreateTime);
        IPage<TOperationLog> operaPage = operationLogMapper.selectPage(PageUtil.convert(requestParam), queryWrapper);
        return PageResponse.<TOperationLog>builder()
                .current(operaPage.getCurrent())
                .size(operaPage.getSize())
                .records(operaPage.getRecords())
                .total(operaPage.getTotal())
                .build();
    }

    @Override
    public Map findOperaLogListByRedis(OperationLogDao requestParam) {
        Page page = PageUtil.convert(requestParam);
        int current =(int) page.getCurrent();
        int size = (int) page.getSize();
        List<String> logs = RedisUtils.getListByPage(GlobalConstants.OPERATION_LOG_KEY, current, size);
        long count = RedisUtils.getListSize(GlobalConstants.OPERATION_LOG_KEY);
        Map map = new HashMap();
        map.put("data", logs);
        map.put("count", count);
        return map;
    }
}




