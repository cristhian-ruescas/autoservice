package com.autoservice.domain.atendimento.events;

import com.autoservice.domain.events.DomainEvent;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.veiculo.VeiculoID;

import java.time.Instant;

public class OrdemServicoAtendimentoAbertaEvent implements DomainEvent {

    private final OrdemServicoID ordemServicoId;
    private final VeiculoID veiculoId;
    private final Instant occurredOn;

    public OrdemServicoAtendimentoAbertaEvent(
            final OrdemServicoID ordemServicoId,
            final VeiculoID veiculoId
    ) {
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
