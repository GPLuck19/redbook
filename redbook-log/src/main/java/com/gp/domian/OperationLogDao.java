package com.gp.domian;

import com.gp.page.PageRequest;
import lombok.Data;

import java.util.Date;

/**
 * 操作日志表
 * @TableName t_operation_log
 */
@Data
public class OperationLogDao extends PageRequest {
    /**
     * 用户编号
     */
    private String operName;

    /**
     * 模块标题
     */
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    private Integer businessType;

    /**
     * 操作开始时间
     */
    private Date startTime;


    /**
     * 操作结束时间
     */
    private Date stopTime;

}