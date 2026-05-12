package com.autoservice.application.ordemservico.diagnostico;

import com.autoservice.application.UseCase;
import com.autoservice.application.ordemservico.status.OrdemServicoStatusOutput;
import com.autoservice.domain.events.DomainEventPublisher;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.ItemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.validation.Error;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class FinalizarDiagnosticoUseCase extends UseCase<FinalizarDiagnosticoCommand, OrdemServicoStatusOutput> {

    private final OrdemServicoGateway ordemServicoGateway;
    private final ItemServicoGateway itemServicoGateway;
    private final DomainEventPublisher eventPublisher;

    public FinalizarDiagnosticoUseCase(
            final OrdemServicoGateway ordemServicoGateway,
            final ItemServicoGateway itemServicoGateway,
            final DomainEventPublisher eventPublisher
    ) {
        this.ordemServicoGateway = Objects.requireNonNull(ordemServicoGateway);
        this.itemServicoGateway = Objects.requireNonNull(itemServicoGateway);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    @Transactional
    public OrdemServicoStatusOutput execute(final FinalizarDiagnosticoCommand command) {
        final var id = OrdemServicoID.from(command.ordemServicoId());

        final var ordemServico = this.ordemServicoGateway.findById(id)
                .orElseThrow(() -> DomainException.with(new Error("Ordem de serviço não encontrada")));

        final BigDecimal valorTotal = this.itemServicoGateway.totalByOrdemServicoId(id);
        if (valorTotal == null || valorTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw DomainException.with(new Error(
                    "Ordem de serviço precisa possuir valor maior que zero para aguardar aprovação"
            ));
        }

        ordemServico.finalizarDiagnostico(
                command.tempoPrevistoExecucaoDias(),
                command.tempoPrevistoExecucaoHoras()
        );

        final var ordemServicoAtualizada = this.ordemServicoGateway.update(ordemServico);

        ordemServicoAtualizada.getDomainEvents().forEach(this.eventPublisher::publishEvent);
        ordemServicoAtualizada.clearEvents();

        return OrdemServicoStatusOutput.from(ordemServicoAtualizada);
    }
}
