package com.autoservice.infrastructure.observability;

import com.autoservice.domain.atendimento.events.OrdemServicoAtendimentoAbertaEvent;
import com.autoservice.domain.ordemservico.events.OrdemServicoStatusAlteradoEvent;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

@Component
public class BusinessMetricsListener {

    private final MeterRegistry meterRegistry;

    public BusinessMetricsListener(final MeterRegistry meterRegistry) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(final OrdemServicoAtendimentoAbertaEvent event) {
        this.meterRegistry.counter(
                "autoservice.service_orders.opened.total",
                "origin", "atendimento"
        ).increment();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(final OrdemServicoStatusAlteradoEvent event) {
        this.meterRegistry.counter(
                "autoservice.service_orders.status_transitions.total",
                "from", event.getStatusAnterior() == null ? "NONE" : event.getStatusAnterior().name(),
                "to", event.getStatusNovo().name()
        ).increment();
    }
}
