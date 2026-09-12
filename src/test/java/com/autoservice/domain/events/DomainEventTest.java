package com.autoservice.domain.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("DomainEvent")
class DomainEventTest {

    @Test
    @DisplayName("Deve garantir a interface DomainEvent")
    void testDomainEvent() {
        DomainEvent event = new DomainEvent() {
            @Override
            public Instant occurredOn() {
                return Instant.now();
            }
        };

        assertNotNull(event.occurredOn());
    }
}
