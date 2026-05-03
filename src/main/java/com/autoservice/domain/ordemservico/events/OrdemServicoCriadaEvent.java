package com.autoservice.domain.ordemservico.events;

import com.autoservice.domain.events.DomainEvent;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.veiculo.VeiculoID;

import java.time.Instant;

public class OrdemServicoCriadaEvent implements DomainEvent {

    private final OrdemServicoID ordemServicoId;
    private final VeiculoID veiculoId;
    private final Instant occurredOn;

    public OrdemServicoCriadaEvent(final OrdemServicoID ordemServicoId, final VeiculoID veiculoId) {
        this.ordemServicoId = ordemServicoId;
        this.veiculoId = veiculoId;
        this.occurredOn = Instant.now();
    }

    public OrdemServicoID getOrdemServicoId() {
        return ordemServicoId;
    }

    public VeiculoID getVeiculoId() {
        return veiculoId;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
