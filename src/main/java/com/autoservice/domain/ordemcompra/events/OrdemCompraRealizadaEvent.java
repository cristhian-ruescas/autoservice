package com.autoservice.domain.ordemcompra.events;

import com.autoservice.domain.events.DomainEvent;
import com.autoservice.domain.ordemcompra.OrdemCompraID;

import java.time.Instant;

public class OrdemCompraRealizadaEvent implements DomainEvent {

    private final OrdemCompraID ordemCompraId;
    private final Instant occurredOn;

    public OrdemCompraRealizadaEvent(final OrdemCompraID ordemCompraId) {
        this.ordemCompraId = ordemCompraId;
        this.occurredOn = Instant.now();
    }

    public OrdemCompraID getOrdemCompraId() {
        return ordemCompraId;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
