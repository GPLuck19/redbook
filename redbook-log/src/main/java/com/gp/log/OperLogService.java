package com.gp.log;

import cn.hutool.core.bean.BeanUtil;
import com.gp.constant.GlobalConstants;
import com.gp.entity.TOperationLog;
import com.gp.event.OperLogEvent;
import com.gp.mapper.TOperationLogMapper;
import com.gp.utils.IpAddressUtils;
import com.gp.utils.LoginHelper;
import com.gp.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 操作日志 服务层处理
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class OperLogService {

    private final TOperationLogMapper operationLogMapper;


    /**
     * 操作日志记录
     * @param operLogEvent 操作日志事件
     */
    @Async
    @EventListener
    public void recordOper(OperLogEvent operLogEvent) {
        TOperationLog operLog = BeanUtil.toBean(operLogEvent, TOperationLog.class);
        // 远程查询操作地点
        operLog.setOperLocation(IpAddressUtils.getRegion(operLog.getOperIp()));
        operLog.setOperTime(new Date());
        operLog.setUserId(LoginHelper.getUserId());
        operationLogMapper.insert(operLog);
        log.info("操作日志插入成功");
    }
}
