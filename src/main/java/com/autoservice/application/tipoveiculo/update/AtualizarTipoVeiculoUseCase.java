package com.autoservice.application.tipoveiculo.update;

import com.autoservice.application.UseCase;
import com.autoservice.application.tipoveiculo.query.TipoVeiculoOutput;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.tipoveiculo.TipoVeiculoGateway;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.domain.veiculo.valueobject.Marca;
import com.autoservice.domain.veiculo.valueobject.Modelo;
import com.autoservice.validation.Error;

import java.util.Objects;

public class AtualizarTipoVeiculoUseCase extends UseCase<AtualizarTipoVeiculoCommand, TipoVeiculoOutput> {

    private final TipoVeiculoGateway tipoVeiculoGateway;

    public AtualizarTipoVeiculoUseCase(final TipoVeiculoGateway tipoVeiculoGateway) {
        this.tipoVeiculoGateway = Objects.requireNonNull(tipoVeiculoGateway);
    }

    @Override
    public TipoVeiculoOutput execute(final AtualizarTipoVeiculoCommand command) {
        if (command == null) {
            throw DomainException.with(new Error("Comando para atualizar tipo de veículo não deve ser nulo"));
        }
        if (command.tipoVeiculoId() == null) {
            throw DomainException.with(new Error("Tipo de veículo é obrigatório para atualização"));
        }

        final var tipoVeiculoId = TipoVeiculoID.from(command.tipoVeiculoId());

        this.tipoVeiculoGateway.findById(tipoVeiculoId)
                .orElseThrow(() -> DomainException.with(new Error("Tipo de veículo não encontrado")));

        final var tipoVeiculoAtualizado = TipoVeiculo.with(
                tipoVeiculoId,
                Marca.from(command.marca()),
                Modelo.from(command.modelo()),
                Ano.from(command.ano())
        );

        return TipoVeiculoOutput.from(this.tipoVeiculoGateway.create(tipoVeiculoAtualizado));
    }
}
