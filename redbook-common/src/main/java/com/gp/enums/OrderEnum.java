package com.gp.enums;

/**
 * @ClassName UserEnum.java
 * @Description TODO
 */
public enum OrderEnum implements IBasicEnum {
    SUCCEED("200","操作成功"),
    FAIL("1000","操作失败"),
    PAGE_FAIL("10001", "订单列表失败"),
    ORDER_BAD("10002", "订单信息有误"),

    ORDER_ADD_BAD("10003", "订单创建异常"),

    ORDER_NEWS_BAD("10004", "订单消息队列异常"),

    ORDER_PAY_WX_BAD("10005", "微信支付失败"),
    ORDER_PAY_WX_OK("10006", "微信支付成功"),


    ;

    private String code;
    private String msg;

    OrderEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}

