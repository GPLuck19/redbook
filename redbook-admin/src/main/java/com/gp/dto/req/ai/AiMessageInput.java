package com.gp.dto.req.ai;

import lombok.Data;

import java.util.List;

@Data
public class AiMessageInput {

    String sessionId;

    String textContent;

    String type;

    List<Medias> medias;

    Long userId;
}
