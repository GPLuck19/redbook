package com.gp.enums;

/**
 * @ClassName UserEnum.java
 * @Description TODO
 */
public enum ExecuteEnum implements IBasicEnum {
    SUCCEED("200","操作成功"),
    FAIL("1000","操作失败"),
    FAIL_2001("2001","策略未定义"),
    ;

    private String code;
    private String msg;

    ExecuteEnum(String code, String msg) {
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

