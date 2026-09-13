package com.autoservice.domain.events;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredOn();
}