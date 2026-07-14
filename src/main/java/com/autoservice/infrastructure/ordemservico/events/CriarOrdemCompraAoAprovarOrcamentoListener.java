package com.autoservice.infrastructure.ordemservico.events;

import com.autoservice.domain.itemservico.ItemServico;
import com.autoservice.domain.itemservico.ItemServicoGateway;
import com.autoservice.domain.ordemcompra.ItemOrdemCompra;
import com.autoservice.domain.ordemcompra.ItemOrdemCompraGateway;
import com.autoservice.domain.ordemcompra.OrdemCompra;
import com.autoservice.domain.ordemcompra.OrdemCompraGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.itemservico.ItemServicoPecaAggregator;
import com.autoservice.domain.ordemservico.events.OrdemServicoOrcamentoAprovadoEvent;
import com.autoservice.domain.peca.PecaID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;
import java.util.Objects;

@Component
public class CriarOrdemCompraAoAprovarOrcamentoListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CriarOrdemCompraAoAprovarOrcamentoListener.class);

    private final ItemServicoGateway itemServicoGateway;
    private final OrdemCompraGateway ordemCompraGateway;
    private final ItemOrdemCompraGateway itemOrdemCompraGateway;

    public CriarOrdemCompraAoAprovarOrcamentoListener(
            final ItemServicoGateway itemServicoGateway,
            final OrdemCompraGateway ordemCompraGateway,
            final ItemOrdemCompraGateway itemOrdemCompraGateway
    ) {
        this.itemServicoGateway = Objects.requireNonNull(itemServicoGateway);
        this.ordemCompraGateway = Objects.requireNonNull(ordemCompraGateway);
        this.itemOrdemCompraGateway = Objects.requireNonNull(itemOrdemCompraGateway);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, fallbackExecution = true)
    public void on(final OrdemServicoOrcamentoAprovadoEvent event) {
        final OrdemServicoID ordemServicoId = event.getOrdemServicoId();

        final Map<PecaID, Integer> quantidadesPorPeca = ItemServicoPecaAggregator.quantidadesPorPeca(
                this.itemServicoGateway,
                ordemServicoId
        );

        if (quantidadesPorPeca.isEmpty()) {
            LOGGER.info(
                    "Ordem de serviço {} aprovada sem peças. Nenhuma ordem de compra será criada.",
                    ordemServicoId.getValue()
            );
            return;
        }

        final var ordemCompra = this.ordemCompraGateway.create(OrdemCompra.newOrdemCompra());

        quantidadesPorPeca.forEach((pecaId, quantidade) -> this.itemOrdemCompraGateway.create(
                ItemOrdemCompra.newItem(ordemCompra.getId(), pecaId, quantidade)
        ));

        LOGGER.info(
                "Criada ordem de compra {} com {} item(ns) para a ordem de serviço {}.",
                ordemCompra.getId().getValue(),
                quantidadesPorPeca.size(),
                ordemServicoId.getValue()
        );
    }
}
