package com.autoservice.application.ordemservico.delete;

import com.autoservice.application.UseCase;
import com.autoservice.application.ordemservico.status.OrdemServicoStatusOutput;
import com.autoservice.domain.events.DomainEventPublisher;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.validation.Error;

import java.util.Objects;

public class RemoverOrdemServicoUseCase extends UseCase<RemoverOrdemServicoCommand, OrdemServicoStatusOutput> {

    private final OrdemServicoGateway ordemServicoGateway;
    private final DomainEventPublisher eventPublisher;

    public RemoverOrdemServicoUseCase(
            final OrdemServicoGateway ordemServicoGateway,
            final DomainEventPublisher eventPublisher
    ) {
        this.ordemServicoGateway = Objects.requireNonNull(ordemServicoGateway);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public OrdemServicoStatusOutput execute(final RemoverOrdemServicoCommand command) {
        if (command == null) {
            throw DomainException.with(new Error("Comando para remover ordem de serviço não deve ser nulo"));
        }
        if (command.ordemServicoId() == null) {
            throw DomainException.with(new Error("Ordem de serviço é obrigatória para remoção"));
        }

        final var ordemServico = this.ordemServicoGateway.findById(OrdemServicoID.from(command.ordemServicoId()))
                .orElseThrow(() -> DomainException.with(new Error("Ordem de serviço não encontrada")));

        ordemServico.cancelar();

        final var ordemServicoAtualizada = this.ordemServicoGateway.create(ordemServico);

        ordemServicoAtualizada.getDomainEvents().forEach(this.eventPublisher::publishEvent);
        ordemServicoAtualizada.clearEvents();

        return OrdemServicoStatusOutput.from(ordemServicoAtualizada);
    }
}
