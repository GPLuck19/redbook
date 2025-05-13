package com.gp.dto.req.user;

import com.gp.page.PageRequest;
import lombok.Data;

@Data
public class FriendMessageDao extends PageRequest {

    private Long userId;

    private Long friendId;


}
