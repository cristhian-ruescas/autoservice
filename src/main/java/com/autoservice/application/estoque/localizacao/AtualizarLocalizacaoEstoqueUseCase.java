package com.autoservice.application.estoque.localizacao;

import com.autoservice.application.UseCase;
import com.autoservice.domain.estoque.EstoqueGateway;
import com.autoservice.domain.estoque.EstoqueID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.Error;

import java.util.Objects;

public class AtualizarLocalizacaoEstoqueUseCase extends UseCase<AtualizarLocalizacaoEstoqueCommand, AtualizarLocalizacaoEstoqueOutput> {

    private final EstoqueGateway estoqueGateway;

    public AtualizarLocalizacaoEstoqueUseCase(final EstoqueGateway estoqueGateway) {
        this.estoqueGateway = Objects.requireNonNull(estoqueGateway);
    }

    @Override
    public AtualizarLocalizacaoEstoqueOutput execute(final AtualizarLocalizacaoEstoqueCommand command) {
        final var estoque = this.estoqueGateway.findById(EstoqueID.from(command.id()))
                .orElseThrow(() -> DomainException.with(new Error("Estoque não encontrado")));

        estoque.alterarLocalizacao(command.localizacao());

        return AtualizarLocalizacaoEstoqueOutput.from(this.estoqueGateway.update(estoque));
    }
}
