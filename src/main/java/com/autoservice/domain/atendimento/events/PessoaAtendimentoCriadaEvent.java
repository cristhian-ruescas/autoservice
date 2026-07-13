package com.autoservice.domain.atendimento.events;

import com.autoservice.domain.events.DomainEvent;
import com.autoservice.domain.pessoa.PessoaID;

import java.time.Instant;

public class PessoaAtendimentoCriadaEvent implements DomainEvent {

    private final PessoaID pessoaId;
    private final String tipoPessoa;
    private final PessoaID representanteLegalId;
    private final Instant occurredOn;

    public PessoaAtendimentoCriadaEvent(
            final PessoaID pessoaId,
            final String tipoPessoa,
            final PessoaID representanteLegalId
    ) {
        this.pessoaId = pessoaId;
        this.tipoPessoa = tipoPessoa;
        this.representanteLegalId = representanteLegalId;
        this.occurredOn = Instant.now();
    }

    public PessoaID getPessoaId() {
        return pessoaId;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public PessoaID getRepresentanteLegalId() {
        return representanteLegalId;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
