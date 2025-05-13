package com.gp.controller.assets;


import com.gp.domian.LoginLogDao;
import com.gp.domian.OperationLogDao;
import com.gp.entity.TLoginLog;
import com.gp.entity.TOperationLog;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TLogService;
import com.gp.service.TLoginLogService;
import com.gp.service.TOperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * 日志维护 控制层
 */
@RestController
@RequiredArgsConstructor
public class SysLogController {

    private final TLoginLogService loginLogService;

    private final TLogService logService;

    private final TOperationLogService operationLogService;
    @PostMapping("/api/assects-service/findLoginLogList")
    public Result<PageResponse<TLoginLog>> findLoginLogList(@RequestBody LoginLogDao requestParam) {
        return Results.success(loginLogService.findLoginLogList(requestParam));
    }

    @PostMapping("/api/assects-service/findOperaLogList")
    public Result<PageResponse<TOperationLog>> findOperaLogList(@RequestBody OperationLogDao requestParam) {
        return Results.success(operationLogService.findOperaLogList(requestParam));
    }

    @PostMapping("/api/assects-service/findLoginLogListByRedis")
    public Result<Map> findLoginLogListByRedis(@RequestBody LoginLogDao requestParam) {
        return Results.success(loginLogService.findLoginLogListByRedis(requestParam));
    }

    @PostMapping("/api/assects-service/findOperaLogListByRedis")
    public Result<Map> findOperaLogListByRedis(@RequestBody OperationLogDao requestParam) {
        return Results.success(operationLogService.findOperaLogListByRedis(requestParam));
    }

    @PostMapping("/api/assects-service/findLoginLogs")
    public Result<PageResponse<TLoginLog>> findLoginLogs(@RequestBody LoginLogDao requestParam) {
        return Results.success(logService.findLoginLogs(requestParam));
    }

    @PostMapping("/api/assects-service/findOperaLogs")
    public Result<PageResponse<TOperationLog>> findOperaLogs(@RequestBody OperationLogDao requestParam) {
        return Results.success(logService.findOperaLogs(requestParam));
    }
}
