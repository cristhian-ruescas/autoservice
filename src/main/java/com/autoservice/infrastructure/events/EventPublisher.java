package com.autoservice.infrastructure.events;

import com.autoservice.domain.events.DomainEvent;
import com.autoservice.domain.events.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher publisher;

    public EventPublisher(final ApplicationEventPublisher publisher) {
        this.publisher = Objects.requireNonNull(publisher);
    }

    @Override
    public void publishEvent(final DomainEvent event) {
        this.publisher.publishEvent(event);
    }
}
