package com.autoservice.infrastructure.events;

import com.autoservice.domain.events.DomainEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventPublisher")
class EventPublisherTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private EventPublisher eventPublisher;

    @Test
    @DisplayName("Deve delegar a publicação do evento para o ApplicationEventPublisher")
    void devePublicarEvento() {
        DomainEvent event = new DomainEvent() {
            @Override
            public Instant occurredOn() {
                return Instant.now();
            }
        };

        eventPublisher.publishEvent(event);

        verify(applicationEventPublisher, times(1)).publishEvent(event);
    }
}
