package com.gp.dto.req.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentsDao {



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



}
