package com.autoservice.application.estoque.localizacao;

import com.autoservice.application.UseCase;
import com.autoservice.domain.estoque.EstoqueGateway;
import com.autoservice.domain.estoque.EstoqueID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AtualizarLocalizacaoEstoqueUseCase extends UseCase<AtualizarLocalizacaoEstoqueCommand, AtualizarLocalizacaoEstoqueOutput> {

    private final EstoqueGateway estoqueGateway;

    public AtualizarLocalizacaoEstoqueUseCase(final EstoqueGateway estoqueGateway) {
        this.estoqueGateway = Objects.requireNonNull(estoqueGateway);
    }

    @Override
    @Transactional
    public AtualizarLocalizacaoEstoqueOutput execute(final AtualizarLocalizacaoEstoqueCommand command) {
        final var estoque = this.estoqueGateway.findById(EstoqueID.from(command.id()))
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado"));

        estoque.alterarLocalizacao(command.localizacao());

        return AtualizarLocalizacaoEstoqueOutput.from(this.estoqueGateway.update(estoque));
    }
}
