package com.autoservice.application.ordemservico.diagnostico;

import com.autoservice.application.UseCase;
import com.autoservice.application.ordemservico.status.OrdemServicoStatusOutput;
import com.autoservice.domain.events.DomainEventPublisher;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class IniciarDiagnosticoUseCase extends UseCase<IniciarDiagnosticoCommand, OrdemServicoStatusOutput> {

    private final OrdemServicoGateway ordemServicoGateway;
    private final DomainEventPublisher eventPublisher;

    public IniciarDiagnosticoUseCase(
            final OrdemServicoGateway ordemServicoGateway,
            final DomainEventPublisher eventPublisher
    ) {
        this.ordemServicoGateway = Objects.requireNonNull(ordemServicoGateway);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    @Transactional
    public OrdemServicoStatusOutput execute(final IniciarDiagnosticoCommand command) {
        final var id = OrdemServicoID.from(command.ordemServicoId());

        final var ordemServico = this.ordemServicoGateway.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ordem de serviço não encontrada"));

        ordemServico.iniciarDiagnostico();

        final var ordemServicoAtualizada = this.ordemServicoGateway.update(ordemServico);

        ordemServicoAtualizada.getDomainEvents().forEach(this.eventPublisher::publishEvent);
        ordemServicoAtualizada.clearEvents();

        return OrdemServicoStatusOutput.from(ordemServicoAtualizada);
    }
}
