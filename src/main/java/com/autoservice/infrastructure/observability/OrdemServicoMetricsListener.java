package com.autoservice.infrastructure.observability;

import com.autoservice.domain.ordemservico.events.OrdemServicoCriadaEvent;
import com.autoservice.domain.ordemservico.events.OrdemServicoStatusAlteradoEvent;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

@Component
public class OrdemServicoMetricsListener {

    static final String METRIC_ABERTURA = "autoservice.ordem_servico.abertura";
    static final String METRIC_TEMPO_FASE = "autoservice.ordem_servico.tempo_fase";
    static final String METRIC_STATUS = "autoservice.ordem_servico.status_transicao";

    private final MeterRegistry meterRegistry;
    private final OrdemServicoStatusPhaseTracker phaseTracker;

    public OrdemServicoMetricsListener(
            final MeterRegistry meterRegistry,
            final OrdemServicoStatusPhaseTracker phaseTracker
    ) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);
        this.phaseTracker = Objects.requireNonNull(phaseTracker);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrdemServicoCriada(final OrdemServicoCriadaEvent event) {
        meterRegistry.counter(METRIC_ABERTURA).increment();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrdemServicoStatusAlterado(final OrdemServicoStatusAlteradoEvent event) {
        final var ordemServicoId = event.getOrdemServicoId().getValue();

        if (event.getStatusAnterior() != null) {
            phaseTracker.encerrarFase(ordemServicoId, event.getStatusAnterior())
                    .ifPresent(duration -> meterRegistry.timer(
                            METRIC_TEMPO_FASE,
                            "fase",
                            OrdemServicoStatusPhaseTracker.nomeFaseParaMetrica(event.getStatusAnterior())
                    ).record(duration));
        }

        phaseTracker.iniciarFase(ordemServicoId, event.getStatusNovo());

        meterRegistry.counter(
                METRIC_STATUS,
                "status",
                event.getStatusNovo().name()
        ).increment();
    }
}
