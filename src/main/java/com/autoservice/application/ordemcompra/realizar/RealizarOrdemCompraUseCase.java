package com.autoservice.application.ordemcompra.realizar;

import com.autoservice.application.UseCase;
import com.autoservice.domain.events.DomainEventPublisher;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemcompra.OrdemCompraGateway;
import com.autoservice.domain.ordemcompra.OrdemCompraID;
import com.autoservice.validation.Error;

import java.util.Objects;

public class RealizarOrdemCompraUseCase extends UseCase<RealizarOrdemCompraCommand, RealizarOrdemCompraOutput> {

    private final OrdemCompraGateway ordemCompraGateway;
    private final DomainEventPublisher eventPublisher;

    public RealizarOrdemCompraUseCase(
            final OrdemCompraGateway ordemCompraGateway,
            final DomainEventPublisher eventPublisher
    ) {
        this.ordemCompraGateway = Objects.requireNonNull(ordemCompraGateway);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public RealizarOrdemCompraOutput execute(final RealizarOrdemCompraCommand command) {
        final var id = OrdemCompraID.from(command.ordemCompraId());

        final var ordemCompra = this.ordemCompraGateway.findById(id)
                .orElseThrow(() -> DomainException.with(new Error("Ordem de compra não encontrada")));

        ordemCompra.realizar();

        final var ordemCompraAtualizada = this.ordemCompraGateway.update(ordemCompra);

        ordemCompraAtualizada.getDomainEvents().forEach(this.eventPublisher::publishEvent);
        ordemCompraAtualizada.clearEvents();

        return RealizarOrdemCompraOutput.from(ordemCompraAtualizada);
    }
}
