package com.autoservice.domain.ordemservico.events;

import com.autoservice.domain.events.DomainEvent;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;

import java.time.Instant;

public class OrdemServicoStatusAlteradoEvent implements DomainEvent {

    private final OrdemServicoID ordemServicoId;
    private final OrdemServicoStatus statusAnterior;
    private final OrdemServicoStatus statusNovo;
    private final Instant occurredOn;

    public OrdemServicoStatusAlteradoEvent(
            final OrdemServicoID ordemServicoId,
            final OrdemServicoStatus statusAnterior,
            final OrdemServicoStatus statusNovo
    ) {
        this.ordemServicoId = ordemServicoId;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.occurredOn = Instant.now();
    }

    public OrdemServicoID getOrdemServicoId() {
        return ordemServicoId;
    }

    public OrdemServicoStatus getStatusAnterior() {
        return statusAnterior;
    }

    public OrdemServicoStatus getStatusNovo() {
        return statusNovo;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
