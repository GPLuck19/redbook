package com.gp.handler.sms;

import cn.hutool.http.HttpStatus;
import com.gp.result.Result;
import com.gp.result.Results;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.comm.exception.SmsBlendException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * SMS异常处理器
 *
 * @author AprilWind
 */
@Slf4j
@RestControllerAdvice
public class SmsExceptionHandler {

    /**
     * sms异常
     */
    @ExceptionHandler(SmsBlendException.class)
    public Result<Void> handleSmsBlendException(SmsBlendException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生sms短信异常.", requestURI, e);
        return Results.failure(String.valueOf(HttpStatus.HTTP_INTERNAL_ERROR), "短信发送失败，请稍后再试...");
    }

}
