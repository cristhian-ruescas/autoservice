package com.autoservice.domain.atendimento.events;

import com.autoservice.domain.events.DomainEvent;
import com.autoservice.domain.pessoa.PessoaID;

import java.time.Instant;

public class RepresentanteLegalAtendimentoCriadoEvent implements DomainEvent {

    private final PessoaID representanteLegalId;
    private final Instant occurredOn;

    public RepresentanteLegalAtendimentoCriadoEvent(final PessoaID representanteLegalId) {
        this.representanteLegalId = representanteLegalId;
        this.occurredOn = Instant.now();
    }

    public PessoaID getRepresentanteLegalId() {
        return representanteLegalId;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
