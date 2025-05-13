package com.gp.dto.req.content;

import com.gp.page.PageRequest;
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
public class PostListDao extends PageRequest {
    /**
     * 文章内容
     */
    private String content;

    private List<Long> tagIdList;

    private Long userId;

    private Boolean isCollection;


}