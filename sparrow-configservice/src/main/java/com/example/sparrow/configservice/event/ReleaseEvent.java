package com.example.sparrow.configservice.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReleaseEvent extends ApplicationEvent {
    private final Long appId;
    private final Long releaseId;

    public ReleaseEvent(Object source, Long appId, Long releaseId) {
        super(source);
        this.appId = appId;
        this.releaseId = releaseId;
    }
}
