package com.autoservice.application.tipoveiculo.create;

import com.autoservice.application.UseCase;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.tipoveiculo.TipoVeiculoGateway;
import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.domain.veiculo.valueobject.Marca;
import com.autoservice.domain.veiculo.valueobject.Modelo;

import java.util.Objects;

public class CadastrarTipoVeiculoUseCase extends UseCase<CadastrarTipoVeiculoCommand, CadastrarTipoVeiculoOutput> {

    private final TipoVeiculoGateway tipoVeiculoGateway;

    public CadastrarTipoVeiculoUseCase(final TipoVeiculoGateway tipoVeiculoGateway) {
        this.tipoVeiculoGateway = Objects.requireNonNull(tipoVeiculoGateway);
    }

    @Override
    public CadastrarTipoVeiculoOutput execute(final CadastrarTipoVeiculoCommand command) {
        final var tipoVeiculo = this.tipoVeiculoGateway.findByMarcaModeloAno(
                command.marca(),
                command.modelo(),
                command.ano()
        ).orElseGet(() -> this.tipoVeiculoGateway.create(TipoVeiculo.newTipoVeiculo(
                Marca.from(command.marca()),
                Modelo.from(command.modelo()),
                Ano.from(command.ano())
        )));

        return CadastrarTipoVeiculoOutput.from(tipoVeiculo);
    }
}
