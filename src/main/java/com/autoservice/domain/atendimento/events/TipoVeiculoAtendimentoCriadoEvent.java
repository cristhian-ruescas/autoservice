package com.autoservice.domain.atendimento.events;

import com.autoservice.domain.events.DomainEvent;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;

import java.time.Instant;

public class TipoVeiculoAtendimentoCriadoEvent implements DomainEvent {

    private final TipoVeiculoID tipoVeiculoId;
    private final Instant occurredOn;

    public TipoVeiculoAtendimentoCriadoEvent(final TipoVeiculoID tipoVeiculoId) {
        this.tipoVeiculoId = tipoVeiculoId;
        this.occurredOn = Instant.now();
    }

    public TipoVeiculoID getTipoVeiculoId() {
        return tipoVeiculoId;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
