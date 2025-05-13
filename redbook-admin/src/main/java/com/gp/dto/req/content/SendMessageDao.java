package com.gp.dto.req.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageDao {


    private String content;

    private Long userId;

    private Long friendId;

    private Integer messageType;

    private Long originalMessageId;

}
