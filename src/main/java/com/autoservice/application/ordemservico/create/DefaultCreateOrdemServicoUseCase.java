package com.autoservice.application.ordemservico.create;

import com.autoservice.application.UseCase;
import com.autoservice.domain.events.DomainEventPublisher;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.veiculo.VeiculoID;

import java.util.Objects;

public class DefaultCreateOrdemServicoUseCase extends UseCase<CreateOrdemServicoCommand, CreateOrdemServicoOutput> {

    private final OrdemServicoGateway ordemServicoGateway;
    private final DomainEventPublisher eventPublisher;

    public DefaultCreateOrdemServicoUseCase(
            final OrdemServicoGateway ordemServicoGateway,
            final DomainEventPublisher eventPublisher
    ) {
        this.ordemServicoGateway = Objects.requireNonNull(ordemServicoGateway);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public CreateOrdemServicoOutput execute(final CreateOrdemServicoCommand aCommand) {
        final var veiculoId = VeiculoID.from(aCommand.veiculoId());

        final var novaOrdem = OrdemServico.newOrdemServico(veiculoId, aCommand.relato());

        final var ordemCriada = this.ordemServicoGateway.create(novaOrdem);

        ordemCriada.getDomainEvents()
                .forEach(this.eventPublisher::publishEvent);

        ordemCriada.clearEvents();

        return CreateOrdemServicoOutput.from(ordemCriada);
    }
}
