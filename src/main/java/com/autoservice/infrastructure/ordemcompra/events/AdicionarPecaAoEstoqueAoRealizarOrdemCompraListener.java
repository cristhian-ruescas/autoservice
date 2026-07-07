package com.autoservice.infrastructure.ordemcompra.events;

import com.autoservice.domain.estoque.Estoque;
import com.autoservice.domain.estoque.EstoqueGateway;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemcompra.ItemOrdemCompra;
import com.autoservice.domain.ordemcompra.ItemOrdemCompraGateway;
import com.autoservice.domain.ordemcompra.events.OrdemCompraRealizadaEvent;
import com.autoservice.infrastructure.peca.PecaGatewayImpl;
import com.autoservice.validation.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

@Component
public class AdicionarPecaAoEstoqueAoRealizarOrdemCompraListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdicionarPecaAoEstoqueAoRealizarOrdemCompraListener.class);

    private final PecaGatewayImpl pecaGateway;
    private final EstoqueGateway estoqueGateway;
    private final ItemOrdemCompraGateway itemOrdemCompraGateway;

    public AdicionarPecaAoEstoqueAoRealizarOrdemCompraListener(
            final PecaGatewayImpl pecaGateway,
            final EstoqueGateway estoqueGateway,
            final ItemOrdemCompraGateway itemOrdemCompraGateway
    ) {
        this.pecaGateway = Objects.requireNonNull(pecaGateway);
        this.estoqueGateway = Objects.requireNonNull(estoqueGateway);
        this.itemOrdemCompraGateway = Objects.requireNonNull(itemOrdemCompraGateway);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(final OrdemCompraRealizadaEvent event) {
        final var itens = this.itemOrdemCompraGateway.findByOrdemCompraId(event.getOrdemCompraId());

        itens.forEach(item -> adicionarAoEstoque(item, event));
    }

    private void adicionarAoEstoque(
            final ItemOrdemCompra item,
            final OrdemCompraRealizadaEvent event
    ) {
        final var peca = this.pecaGateway.findById(item.getPecaId())
                .orElseThrow(() -> DomainException.with(new Error("Peça da ordem de compra não encontrada")));
        final int quantidade = item.getQuantidade();

        if (peca.getEstoqueId() == null) {
            final var estoque = this.estoqueGateway.create(Estoque.newEstoque(
                    quantidade,
                    0,
                    null
            ));
            peca.vincularEstoque(estoque.getId());
            this.pecaGateway.create(peca);

            LOGGER.info(
                    "Criado estoque {} para peça {} ao realizar ordem de compra {}.",
                    estoque.getId().getValue(),
                    peca.getId().getValue(),
                    event.getOrdemCompraId().getValue()
            );
            return;
        }

        final var estoque = this.estoqueGateway.findById(peca.getEstoqueId())
                .orElseThrow(() -> DomainException.with(new Error("Estoque da peça não encontrado")));

        estoque.adicionar(quantidade);
        this.estoqueGateway.create(estoque);

        LOGGER.info(
                "Adicionada {} unidade ao estoque {} da peça {} ao realizar ordem de compra {}.",
                quantidade,
                estoque.getId().getValue(),
                peca.getId().getValue(),
                event.getOrdemCompraId().getValue()
        );
    }
}
