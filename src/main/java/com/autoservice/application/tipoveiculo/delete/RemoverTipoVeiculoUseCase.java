package com.autoservice.application.tipoveiculo.delete;

import com.autoservice.application.UnitUseCase;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.tipoveiculo.TipoVeiculoGateway;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.validation.Error;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class RemoverTipoVeiculoUseCase extends UnitUseCase<RemoverTipoVeiculoCommand> {

    private final TipoVeiculoGateway tipoVeiculoGateway;

    public RemoverTipoVeiculoUseCase(final TipoVeiculoGateway tipoVeiculoGateway) {
        this.tipoVeiculoGateway = Objects.requireNonNull(tipoVeiculoGateway);
    }

    @Override
    @Transactional
    public void execute(final RemoverTipoVeiculoCommand command) {
        if (command == null) {
            throw DomainException.with(new Error("Comando para remover tipo de veículo não deve ser nulo"));
        }
        if (command.tipoVeiculoId() == null) {
            throw DomainException.with(new Error("Tipo de veículo é obrigatório para remoção"));
        }

        final var tipoVeiculoId = TipoVeiculoID.from(command.tipoVeiculoId());

        this.tipoVeiculoGateway.findById(tipoVeiculoId)
                .orElseThrow(() -> DomainException.with(new Error("Tipo de veículo não encontrado")));

        this.tipoVeiculoGateway.deleteById(tipoVeiculoId);
    }
}
