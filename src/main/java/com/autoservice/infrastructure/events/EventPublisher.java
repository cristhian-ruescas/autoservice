package com.autoservice.infrastructure.events;

import com.autoservice.domain.events.DomainEvent;
import com.autoservice.domain.events.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Objects;

@Component
public class EventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher publisher;

    public EventPublisher(final ApplicationEventPublisher publisher) {
        this.publisher = Objects.requireNonNull(publisher);
    }

    @Override
    public void publishEvent(final DomainEvent event) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    EventPublisher.this.publisher.publishEvent(event);
                }
            });
            return;
        }

        this.publisher.publishEvent(event);
    }
}
