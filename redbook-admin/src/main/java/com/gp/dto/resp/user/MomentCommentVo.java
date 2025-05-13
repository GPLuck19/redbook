package com.gp.dto.resp.user;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 
 * @TableName
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MomentCommentVo {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 动态ID
     */
    private Long momentId;

    /**
     * 评论的用户ID
     */
    private Long userId;

    /**
     * 评论内容
     */
    private String content;

    // 子评论列表，非数据库字段
    private List<MomentCommentVo> replies;

    private String region;

    private String realName;

    private Date createTime;

}