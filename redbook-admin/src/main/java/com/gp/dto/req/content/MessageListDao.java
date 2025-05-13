package com.gp.dto.req.content;

import com.gp.page.PageRequest;
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
public class MessageListDao extends PageRequest {

    private Long userId;

}