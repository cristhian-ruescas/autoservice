package com.autoservice.application.veiculo.delete;

import com.autoservice.application.UnitUseCase;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.veiculo.VeiculoGateway;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.validation.Error;

import java.util.Objects;

public class RemoverVeiculoUseCase extends UnitUseCase<RemoverVeiculoCommand> {

    private final VeiculoGateway veiculoGateway;

    public RemoverVeiculoUseCase(final VeiculoGateway veiculoGateway) {
        this.veiculoGateway = Objects.requireNonNull(veiculoGateway);
    }

    @Override
    public void execute(final RemoverVeiculoCommand command) {
        if (command == null) {
            throw DomainException.with(new Error("Comando para remover veículo não deve ser nulo"));
        }
        if (command.veiculoId() == null) {
            throw DomainException.with(new Error("Veículo é obrigatório para remoção"));
        }

        final var veiculoId = VeiculoID.from(command.veiculoId());

        this.veiculoGateway.findById(veiculoId)
                .orElseThrow(() -> DomainException.with(new Error("Veículo não encontrado")));

        this.veiculoGateway.deleteById(veiculoId);
    }
}
