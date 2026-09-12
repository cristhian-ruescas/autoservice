package com.autoservice.application.cliente.delete;

import com.autoservice.application.UnitUseCase;
import com.autoservice.domain.cliente.ClienteGateway;
import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.Error;

import java.util.Objects;

public class RemoverClienteUseCase extends UnitUseCase<RemoverClienteCommand> {

    private final ClienteGateway clienteGateway;

    public RemoverClienteUseCase(final ClienteGateway clienteGateway) {
        this.clienteGateway = Objects.requireNonNull(clienteGateway);
    }

    @Override
    public void execute(final RemoverClienteCommand command) {
        if (command == null) {
            throw DomainException.with(new Error("Comando para remover cliente não deve ser nulo"));
        }
        if (command.clienteId() == null) {
            throw DomainException.with(new Error("Cliente é obrigatório para remoção"));
        }

        final var clienteId = ClienteID.from(command.clienteId());

        this.clienteGateway.findById(clienteId)
                .orElseThrow(() -> DomainException.with(new Error("Cliente não encontrado")));

        this.clienteGateway.deleteById(clienteId);
    }
}
