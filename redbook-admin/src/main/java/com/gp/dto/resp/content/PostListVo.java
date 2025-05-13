package com.gp.dto.resp.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 
 * @TableName U_Order_1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostListVo {

    private String userName;

    /**
     * 文章标题
     */
    private String title;

    private String userId;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章配图路径（如果有）
     */
    private String image;

    private Long postId;


    private Date createTime;

    private Date updateTime;

    private Long likeCount;

    private List<String> tagList;

    private Boolean isCollection;

}