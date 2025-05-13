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
public class ForwardPostDao {

    /**
     * 发送人ID
     */
    private Long pushId;

    /**
     * 接收人ID
     */
    private List<Long> receiverIds;

    /**
     * 标题
     */
    private String title;


    /**
     * 文章id
     */
    private Long postId;

}