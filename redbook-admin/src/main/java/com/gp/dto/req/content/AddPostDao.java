package com.gp.dto.req.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 
 * @TableName U_Order_1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPostDao {

    /**
     * 作者用户 ID
     */
    private Long userId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章配图路径（如果有）
     */
    private List<String> images;

    private Long id;

    private String username;

    private List<Long> tagIdList;

}