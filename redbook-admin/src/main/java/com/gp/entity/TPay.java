package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付表
 * @TableName tb_pay
 */
@TableName(value ="t_pay")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TPay extends BaseDO {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    /**
     * 订单号
     */
    @TableField(value = "order_sn")
    private String orderSn;

    /**
     * 支付人id
     */
    @TableField(value = "userId")
    private Long userId;


    /**
     * 支付渠道
     */
    @TableField(value = "channel")
    private String channel;


    /**
     * 订单标题
     */
    @TableField(value = "subject")
    private String subject;


    /**
     * 交易总金额
     */
    @TableField(value = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 三方交易凭证号
     */
    @TableField(value = "trade_no")
    private String tradeNo;

    /**
     * 付款时间
     */
    @TableField(value = "gmt_payment")
    private Date gmtPayment;

    /**
     * 支付金额
     */
    @TableField(value = "pay_amount")
    private BigDecimal payAmount;

    /**
     * 支付状态 1.支付 0.未支付
     */
    @TableField(value = "status")
    private Integer status;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}