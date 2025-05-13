package com.gp.enums;

/**
 * @ClassName UserEnum.java
 * @Description TODO
 */
public enum GoodsEnum implements IBasicEnum {
    SUCCEED("200","操作成功"),
    FAIL("1000","操作失败"),
    PAGE_FAIL("10001", "查询商品列表失败"),
    GOODS_BAD("10002", "商品信息有误"),

    GOODS_DELIST("10003", "商品已经下架"),

    GOODS_ADD_BAD("10004", "商品下单异常"),

    GOODS_LUCK_BAD("10005", "商品下单上锁异常"),

    GOODS_NUMBER_BAD("10006", "商品库存不足"),

    ;

    private String code;
    private String msg;

    GoodsEnum(String code, String msg) {
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

