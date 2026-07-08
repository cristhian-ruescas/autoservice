package com.autoservice.application.peca.create;

import com.autoservice.application.UseCase;
import com.autoservice.domain.estoque.Estoque;
import com.autoservice.domain.estoque.EstoqueGateway;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.peca.PecaGateway;
import com.autoservice.domain.tipoveiculo.TipoVeiculoGateway;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.validation.Error;

import java.util.Objects;

public class CadastrarPecaUseCase extends UseCase<CadastrarPecaCommand, CadastrarPecaOutput> {

    private final PecaGateway pecaGateway;
    private final EstoqueGateway estoqueGateway;
    private final TipoVeiculoGateway tipoVeiculoGateway;

    public CadastrarPecaUseCase(
            final PecaGateway pecaGateway,
            final EstoqueGateway estoqueGateway,
            final TipoVeiculoGateway tipoVeiculoGateway
    ) {
        this.pecaGateway = Objects.requireNonNull(pecaGateway);
        this.estoqueGateway = Objects.requireNonNull(estoqueGateway);
        this.tipoVeiculoGateway = Objects.requireNonNull(tipoVeiculoGateway);
    }

    @Override
    public CadastrarPecaOutput execute(final CadastrarPecaCommand command) {
        final var estoque = this.estoqueGateway.create(Estoque.newEstoque(
                command.quantidadeEstoque(),
                0,
                null
        ));

        final var peca = Peca.newPeca(
                command.descricao(),
                command.codigo(),
                command.marca(),
                command.valorUnitario(),
                estoque.getId(),
                tipoVeiculoIdOf(command)
        );
        final var pecaCriada = this.pecaGateway.create(peca);

        return CadastrarPecaOutput.from(pecaCriada, estoque.getQuantidadeDisponivel());
    }

    private TipoVeiculoID tipoVeiculoIdOf(final CadastrarPecaCommand command) {
        if (command.tipoVeiculoId() == null) {
            return null;
        }

        final var tipoVeiculoId = TipoVeiculoID.from(command.tipoVeiculoId());
        this.tipoVeiculoGateway.findById(tipoVeiculoId)
                .orElseThrow(() -> DomainException.with(new Error("Tipo de veículo não encontrado")));

        return tipoVeiculoId;
    }
}
