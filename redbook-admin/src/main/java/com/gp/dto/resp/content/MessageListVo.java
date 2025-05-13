package com.gp.dto.resp.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName U_Order_1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageListVo {

    private Long id;

    private Long receiverId;

    private Long pushId;

    private String message;

    private Integer status;

    private Long objectId;

    /**
     * 1.评论 2.点赞  3.转发
     */
    private Integer type;


}