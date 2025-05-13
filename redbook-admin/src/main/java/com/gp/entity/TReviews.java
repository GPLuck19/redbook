package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.gp.base.BaseDO;
import lombok.Builder;
import lombok.Data;

/**
 * 审核表
 * @TableName t_reviews
 */
@TableName(value ="t_reviews")
@Data
@Builder
public class TReviews extends BaseDO {
    /**
     * 审核记录 ID
     */
    @TableId(value = "review_id", type = IdType.AUTO)
    private Long reviewId;

    /**
     * 被审核内容的 ID
     */
    @TableField(value = "target_id")
    private Long targetId;

    /**
     * 被审核内容的类型（如视频、帖子等）
     */
    @TableField(value = "target_type")
    private String targetType;

    /**
     * 审核员的用户 ID
     */
    @TableField(value = "reviewer_id")
    private Long reviewerId;

    /**
     * 审核员的用户名
     */
    @TableField(value = "reviewer_name")
    private String reviewerName;

    /**
     * 审核状态：0 待审核，1 通过，2 拒绝
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 审核拒绝的原因
     */
    @TableField(value = "reason")
    private String reason;

    /**
     * 审核拒绝的备注
     */
    @TableField(value = "remark")
    private String remark;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}