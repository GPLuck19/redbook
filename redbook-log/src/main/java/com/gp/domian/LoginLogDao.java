package com.gp.domian;

import com.gp.page.PageRequest;
import lombok.Data;

import java.util.Date;

/**
 * 用户登录信息表
 * @TableName t_login_log
 */

@Data
public class LoginLogDao extends PageRequest {

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 操作开始时间
     */
    private Date startTime;


    /**
     * 操作结束时间
     */
    private Date stopTime;
}