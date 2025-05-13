package com.gp.dto.req.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MomentCommentsDao {


    /**
     * 父级评论 ID
     */
    private Long parentId;

    /**
     * 朋友圈 ID
     */
    private Long momentId;

    /**
     * 评论用户 ID
     */
    private Long userId;

    /**
     * 评论内容
     */
    private String content;



}
