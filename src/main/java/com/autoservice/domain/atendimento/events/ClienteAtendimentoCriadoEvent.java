package com.autoservice.domain.atendimento.events;

import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.domain.events.DomainEvent;
import com.autoservice.domain.pessoa.PessoaID;

import java.time.Instant;

public class ClienteAtendimentoCriadoEvent implements DomainEvent {

    private final ClienteID clienteId;
    private final PessoaID pessoaId;
    private final Instant occurredOn;

    public ClienteAtendimentoCriadoEvent(final ClienteID clienteId, final PessoaID pessoaId) {
        this.clienteId = clienteId;
        this.pessoaId = pessoaId;
        this.occurredOn = Instant.now();
    }

    public ClienteID getClienteId() {
        return clienteId;
    }

    public PessoaID getPessoaId() {
        return pessoaId;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
