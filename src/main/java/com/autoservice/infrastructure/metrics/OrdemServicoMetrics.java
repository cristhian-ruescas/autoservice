package com.autoservice.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrdemServicoMetrics {

    private final Counter falhasProcessamento;

    public OrdemServicoMetrics(final MeterRegistry meterRegistry) {
        this.falhasProcessamento = Counter.builder("autoservice_os_processamento_falhas_total")
                .description("Falhas no processamento de ordens de serviço (regras de domínio / erros)")
                .register(meterRegistry);
    }

    public void incrementarFalhaProcessamento() {
        this.falhasProcessamento.increment();
    }
}
