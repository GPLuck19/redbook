package com.gp.dto.req.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSendMessageDao {


    private String content;

    private Long userId;

    private Long friendId;

    private Integer messageType;

    private Long originalMessageId;

}
