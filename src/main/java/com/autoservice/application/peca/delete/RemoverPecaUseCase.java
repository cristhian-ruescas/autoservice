package com.autoservice.application.peca.delete;

import com.autoservice.application.UnitUseCase;
import com.autoservice.domain.estoque.EstoqueGateway;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.ItemServicoGateway;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.peca.PecaGateway;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.validation.Error;

import java.util.Objects;

public class RemoverPecaUseCase extends UnitUseCase<RemoverPecaCommand> {

    private final PecaGateway pecaGateway;
    private final EstoqueGateway estoqueGateway;
    private final ItemServicoGateway itemServicoGateway;

    public RemoverPecaUseCase(
            final PecaGateway pecaGateway,
            final EstoqueGateway estoqueGateway,
            final ItemServicoGateway itemServicoGateway
    ) {
        this.pecaGateway = Objects.requireNonNull(pecaGateway);
        this.estoqueGateway = Objects.requireNonNull(estoqueGateway);
        this.itemServicoGateway = Objects.requireNonNull(itemServicoGateway);
    }

    @Override
    public void execute(final RemoverPecaCommand command) {
        if (command == null) {
            throw DomainException.with(new Error("Comando para remover peça não deve ser nulo"));
        }
        if (command.pecaId() == null) {
            throw DomainException.with(new Error("Peça é obrigatória para remoção"));
        }

        final var pecaId = PecaID.from(command.pecaId());

        final var peca = this.pecaGateway.findById(pecaId)
                .orElseThrow(() -> DomainException.with(new Error("Peça não encontrada")));

        if (peca.getEstoqueId() != null) {
            final var estoque = this.estoqueGateway.findById(peca.getEstoqueId()).orElse(null);
            if (estoque != null && estoque.getQuantidadeDisponivel() > 0) {
                throw DomainException.with(new Error("Peça não pode ser removida pois possui saldo em estoque"));
            }
        }

        final boolean emOrdemNaoEntregue = this.itemServicoGateway.existsByPecaIdAndOrdemServicoStatusNot(
                pecaId,
                OrdemServicoStatus.ENTREGUE
        );
        if (emOrdemNaoEntregue) {
            throw DomainException.with(new Error(
                    "Peça não pode ser removida pois está vinculada a ordem de serviço não entregue"
            ));
        }

        this.pecaGateway.deleteById(pecaId);
    }
}
