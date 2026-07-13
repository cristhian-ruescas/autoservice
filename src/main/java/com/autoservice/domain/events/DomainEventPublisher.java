package com.autoservice.domain.events;

public interface DomainEventPublisher {
    void publishEvent(DomainEvent event);
}