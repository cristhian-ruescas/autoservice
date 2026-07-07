package com.autoservice.application.peca.update;

import com.autoservice.application.UseCase;
import com.autoservice.application.peca.query.PecaOutput;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.domain.tipoveiculo.TipoVeiculoGateway;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.infrastructure.peca.PecaGatewayImpl;
import com.autoservice.validation.Error;

import java.util.Objects;

public class AtualizarPecaUseCase extends UseCase<AtualizarPecaCommand, PecaOutput> {

    private final PecaGatewayImpl pecaGateway;
    private final TipoVeiculoGateway tipoVeiculoGateway;

    public AtualizarPecaUseCase(
            final PecaGatewayImpl pecaGateway,
            final TipoVeiculoGateway tipoVeiculoGateway
    ) {
        this.pecaGateway = Objects.requireNonNull(pecaGateway);
        this.tipoVeiculoGateway = Objects.requireNonNull(tipoVeiculoGateway);
    }

    @Override
    public PecaOutput execute(final AtualizarPecaCommand command) {
        if (command == null) {
            throw DomainException.with(new Error("Comando para atualizar peça não deve ser nulo"));
        }
        if (command.pecaId() == null) {
            throw DomainException.with(new Error("Peça é obrigatória para atualização"));
        }

        final var pecaId = PecaID.from(command.pecaId());
        final var pecaAtual = this.pecaGateway.findById(pecaId)
                .orElseThrow(() -> DomainException.with(new Error("Peça não encontrada")));

        final var pecaAtualizada = Peca.with(
                pecaId,
                command.descricao() == null ? pecaAtual.getDescricao() : command.descricao(),
                command.codigo() == null ? pecaAtual.getCodigo() : command.codigo(),
                command.marca() == null ? pecaAtual.getMarca() : command.marca(),
                command.valorUnitario() == null ? pecaAtual.getValorUnitario() : command.valorUnitario(),
                pecaAtual.getEstoqueId(),
                tipoVeiculoIdOf(command, pecaAtual)
        );

        return PecaOutput.from(this.pecaGateway.create(pecaAtualizada));
    }

    private TipoVeiculoID tipoVeiculoIdOf(final AtualizarPecaCommand command, final Peca pecaAtual) {
        if (command.tipoVeiculoId() == null) {
            return pecaAtual.getTipoVeiculoId();
        }

        final var tipoVeiculoId = TipoVeiculoID.from(command.tipoVeiculoId());
        this.tipoVeiculoGateway.findById(tipoVeiculoId)
                .orElseThrow(() -> DomainException.with(new Error("Tipo de veículo não encontrado")));

        return tipoVeiculoId;
    }
}
