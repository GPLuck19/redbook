package com.gp.listener.event;

import lombok.*;
import org.springframework.context.ApplicationEvent;


public class DeleteEvent extends ApplicationEvent {

    @Getter
    @Setter
    private Long targetId;

    @Getter
    @Setter
    private String eventType;

    public DeleteEvent(Object source, Long targetId, String eventType) {
        super(source);
        this.targetId = targetId;
        this.eventType = eventType;
    }


}
