package com.gp.dto.req.ai;

import lombok.Data;

@Data
public class AiMessageParams {
    Boolean enableVectorStore;
    Boolean enableAgent;
}
