package com.gp.dto.resp.pay;
import com.gp.enums.OrderEnum;
import lombok.Data;


/**
 * <p></p>
 *
 * @Description:
 */
@Data
public class PayCodeUrlResult {
    public static final String WX_PAY_SUCCESS_FLAG = "SUCCESS";
    public static final String WX_PAY_FLAG = "WX";
    public static final String AlI_PAY_FLAG = "ALI";
    
    public static final Integer PAIED = 1;
    public static final Integer NOT_PAY = 0;

    private String status;

    private String codeURL;

    private String message;
    /*
    * 成功获得支付url数据
    * */
    public static PayCodeUrlResult success(String codeURL) {
        PayCodeUrlResult result = new PayCodeUrlResult();
        result.setStatus(OrderEnum.ORDER_PAY_WX_OK.getMsg());
        result.setCodeURL(codeURL);
        return result;
    }
    /*
     * 失败获得支付url数据
     * */
    public static PayCodeUrlResult failed(String message) {
        PayCodeUrlResult result = new PayCodeUrlResult();
        result.setStatus(OrderEnum.ORDER_PAY_WX_BAD.getMsg());
        result.setMessage(message);
        return result;
    }
}