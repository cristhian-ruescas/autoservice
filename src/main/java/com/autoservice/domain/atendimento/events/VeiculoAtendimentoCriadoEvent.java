package com.autoservice.domain.atendimento.events;

import com.autoservice.domain.events.DomainEvent;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.VeiculoID;

import java.time.Instant;

public class VeiculoAtendimentoCriadoEvent implements DomainEvent {

    private final VeiculoID veiculoId;
    private final PessoaID proprietarioId;
    private final TipoVeiculoID tipoVeiculoId;
    private final Instant occurredOn;

    public VeiculoAtendimentoCriadoEvent(
            final VeiculoID veiculoId,
            final PessoaID proprietarioId,
            final TipoVeiculoID tipoVeiculoId
    ) {
        this.veiculoId = veiculoId;
        this.proprietarioId = proprietarioId;
        this.tipoVeiculoId = tipoVeiculoId;
        this.occurredOn = Instant.now();
    }

    public VeiculoID getVeiculoId() {
        return veiculoId;
    }

    public PessoaID getProprietarioId() {
        return proprietarioId;
    }

    public TipoVeiculoID getTipoVeiculoId() {
        return tipoVeiculoId;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
