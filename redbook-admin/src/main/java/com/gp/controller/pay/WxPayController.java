package com.gp.controller.pay;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.gp.convert.PayRequestConvert;
import com.gp.dto.port.PayRequest;
import com.gp.dto.req.pay.PayCommand;
import com.gp.dto.resp.pay.PayCodeUrlResult;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TbPayWxService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WxPayController {

    private final TbPayWxService tbPayWxService;

    /**
     * 公共支付接口
     */
    @PostMapping("/api/wx-pay/create")
    public Result<PayCodeUrlResult> pay(@RequestBody PayCommand requestParam) {
        PayRequest payRequest = PayRequestConvert.command2PayRequest(requestParam);
        PayCodeUrlResult result = tbPayWxService.commonPay(payRequest);
        return Results.success(result);
    }


    /* 给wx支付平台来调用的方法，没有在Api接口中定义方法 */
    @PostMapping("/api/wx-pay/notify-result")
    public String notifyPayment(HttpServletRequest request) {
        System.out.println("订单支付通知方法开始执行");
        try {
            String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            // 调用service完成支付通知的业务操作
            tbPayWxService.notifyPayResult(xmlResult);
            return WxPayNotifyResponse.success("处理成功!");
        } catch (Exception e) {
            log.error("微信回调结果异常,异常原因{}", e.getMessage());
            return WxPayNotifyResponse.fail(e.getMessage());
        }
    }
}
