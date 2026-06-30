package com.autoservice.infrastructure.ordemservico.events;

import com.autoservice.domain.estoque.EstoqueGateway;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.ItemServicoGateway;
import com.autoservice.domain.itemservico.ItemServicoPecaAggregator;
import com.autoservice.domain.ordemservico.events.OrdemServicoFinalizadaEvent;
import com.autoservice.domain.peca.PecaGateway;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.validation.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;
import java.util.Objects;

@Component
public class BaixarPecasDoEstoqueAoFinalizarOrdemServicoListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaixarPecasDoEstoqueAoFinalizarOrdemServicoListener.class);

    private final ItemServicoGateway itemServicoGateway;
    private final PecaGateway pecaGateway;
    private final EstoqueGateway estoqueGateway;

    public BaixarPecasDoEstoqueAoFinalizarOrdemServicoListener(
            final ItemServicoGateway itemServicoGateway,
            final PecaGateway pecaGateway,
            final EstoqueGateway estoqueGateway
    ) {
        this.itemServicoGateway = Objects.requireNonNull(itemServicoGateway);
        this.pecaGateway = Objects.requireNonNull(pecaGateway);
        this.estoqueGateway = Objects.requireNonNull(estoqueGateway);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(final OrdemServicoFinalizadaEvent event) {
        final Map<PecaID, Integer> quantidadesPorPeca = ItemServicoPecaAggregator.quantidadesPorPeca(
                this.itemServicoGateway,
                event.getOrdemServicoId()
        );

        if (quantidadesPorPeca.isEmpty()) {
            LOGGER.info(
                    "Ordem de serviço {} finalizada sem peças para baixa de estoque.",
                    event.getOrdemServicoId().getValue()
            );
            return;
        }

        quantidadesPorPeca.forEach((pecaId, quantidade) -> baixarEstoque(event, pecaId, quantidade));
    }

    private void baixarEstoque(
            final OrdemServicoFinalizadaEvent event,
            final PecaID pecaId,
            final Integer quantidade
    ) {
        final var peca = this.pecaGateway.findById(pecaId)
                .orElseThrow(() -> DomainException.with(new Error("Peça da ordem de serviço não encontrada")));

        if (peca.getEstoqueId() == null) {
            throw DomainException.with(new Error("Peça da ordem de serviço não possui estoque vinculado"));
        }

        final var estoque = this.estoqueGateway.findById(peca.getEstoqueId())
                .orElseThrow(() -> DomainException.with(new Error("Estoque da peça não encontrado")));

        estoque.baixar(quantidade);
        this.estoqueGateway.update(estoque);

        LOGGER.info(
                "Baixada(s) {} unidade(s) do estoque {} para a peça {} na finalização da ordem de serviço {}.",
                quantidade,
                estoque.getId().getValue(),
                peca.getId().getValue(),
                event.getOrdemServicoId().getValue()
        );
    }
}
