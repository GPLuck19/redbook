package com.gp.service;

import com.gp.dto.port.PayRequest;
import com.gp.dto.resp.pay.PayCodeUrlResult;

/**
* @author Administrator
* @description 针对表【tb_pay_wx】的数据库操作Service
* @createDate 2024-09-20 14:20:01
*/
public interface TbPayWxService  {

    PayCodeUrlResult commonPay(PayRequest payRequest);

    void notifyPayResult(String xmlResult);
}
