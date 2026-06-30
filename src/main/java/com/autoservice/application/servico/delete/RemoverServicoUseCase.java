package com.autoservice.application.servico.delete;

import com.autoservice.application.UnitUseCase;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.servico.ServicoGateway;
import com.autoservice.domain.servico.ServicoID;
import com.autoservice.validation.Error;

import java.util.Objects;

public class RemoverServicoUseCase extends UnitUseCase<RemoverServicoCommand> {

    private final ServicoGateway servicoGateway;

    public RemoverServicoUseCase(final ServicoGateway servicoGateway) {
        this.servicoGateway = Objects.requireNonNull(servicoGateway);
    }

    @Override
    public void execute(final RemoverServicoCommand command) {
        if (command == null || command.servicoId() == null) {
            throw DomainException.with(new Error("Serviço é obrigatório para remoção"));
        }

        final var id = ServicoID.from(command.servicoId());
        if (this.servicoGateway.findById(id).isEmpty()) {
            throw DomainException.with(new Error("Serviço não encontrado"));
        }

        this.servicoGateway.deleteById(id);
    }
}
