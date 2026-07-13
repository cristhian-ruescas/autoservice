package com.autoservice.domain.ordemservico.events;

import com.autoservice.domain.events.DomainEvent;
import com.autoservice.domain.ordemservico.OrdemServicoID;

import java.time.Instant;

public class OrdemServicoDiagnosticoIniciadoEvent implements DomainEvent {

    private final OrdemServicoID ordemServicoId;
    private final Instant occurredOn;

    public OrdemServicoDiagnosticoIniciadoEvent(final OrdemServicoID ordemServicoId) {
        this.ordemServicoId = ordemServicoId;
        this.occurredOn = Instant.now();
    }

    public OrdemServicoID getOrdemServicoId() {
        return ordemServicoId;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
