package com.gp.dto.resp.content;


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
public class CommentVo {

    /**
     * 评论 ID
     */
    private Long commentId;


    /**
     * 父级评论 ID
     */
    private Long parentId;

    /**
     * 文章 ID
     */
    private Long postId;

    /**
     * 评论用户 ID
     */
    private Long userId;

    /**
     * 评论内容
     */
    private String content;

    // 子评论列表，非数据库字段
    private List<CommentVo> replies;

    private String region;

    private String realName;

    private Date createTime;

}