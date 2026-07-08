package com.autoservice.application.ordemservico.entrega;

import com.autoservice.application.UseCase;
import com.autoservice.application.ordemservico.status.OrdemServicoStatusOutput;
import com.autoservice.domain.events.DomainEventPublisher;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.validation.Error;

import java.util.Objects;

public class EntregarOrdemServicoUseCase extends UseCase<EntregarOrdemServicoCommand, OrdemServicoStatusOutput> {

    private final OrdemServicoGateway ordemServicoGateway;
    private final DomainEventPublisher eventPublisher;

    public EntregarOrdemServicoUseCase(
            final OrdemServicoGateway ordemServicoGateway,
            final DomainEventPublisher eventPublisher
    ) {
        this.ordemServicoGateway = Objects.requireNonNull(ordemServicoGateway);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public OrdemServicoStatusOutput execute(final EntregarOrdemServicoCommand command) {
        final var id = OrdemServicoID.from(command.ordemServicoId());

        final var ordemServico = this.ordemServicoGateway.findById(id)
                .orElseThrow(() -> DomainException.with(new Error("Ordem de serviço não encontrada")));

        ordemServico.entregar();

        final var ordemServicoAtualizada = this.ordemServicoGateway.update(ordemServico);

        ordemServicoAtualizada.getDomainEvents().forEach(this.eventPublisher::publishEvent);
        ordemServicoAtualizada.clearEvents();

        return OrdemServicoStatusOutput.from(ordemServicoAtualizada);
    }
}
