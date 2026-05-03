package com.autoservice.infrastructure.ordemservico.events;

import com.autoservice.application.ordemservico.detail.DetailOrdemServicoQuery;
import com.autoservice.application.ordemservico.orcamento.OrcamentoEmailSender;
import com.autoservice.application.ordemservico.orcamento.OrcamentoPdfGenerator;
import com.autoservice.domain.ordemservico.events.OrdemServicoDiagnosticoFinalizadoEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;
import java.util.UUID;

@Component
public class EnviarOrcamentoAoFinalizarDiagnosticoListener {

    private final DetailOrdemServicoQuery detailOrdemServicoQuery;
    private final OrcamentoPdfGenerator orcamentoPdfGenerator;
    private final OrcamentoEmailSender orcamentoEmailSender;

    public EnviarOrcamentoAoFinalizarDiagnosticoListener(
            final DetailOrdemServicoQuery detailOrdemServicoQuery,
            final OrcamentoPdfGenerator orcamentoPdfGenerator,
            final OrcamentoEmailSender orcamentoEmailSender
    ) {
        this.detailOrdemServicoQuery = Objects.requireNonNull(detailOrdemServicoQuery);
        this.orcamentoPdfGenerator = Objects.requireNonNull(orcamentoPdfGenerator);
        this.orcamentoEmailSender = Objects.requireNonNull(orcamentoEmailSender);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(final OrdemServicoDiagnosticoFinalizadoEvent event) {
        final var ordemServico = this.detailOrdemServicoQuery.execute(
                UUID.fromString(event.getOrdemServicoId().getValue())
        );
        final byte[] pdf = this.orcamentoPdfGenerator.generate(ordemServico);

        this.orcamentoEmailSender.send(ordemServico, pdf);
    }
}
