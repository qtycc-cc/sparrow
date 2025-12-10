package com.example.sparrow.configservice.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReleaseEvent extends ApplicationEvent {
    private final Long namespaceId;
    private final Long releaseId;

    public ReleaseEvent(Object source, Long namespaceId, Long releaseId) {
        super(source);
        this.namespaceId = namespaceId;
        this.releaseId = releaseId;
    }
}
