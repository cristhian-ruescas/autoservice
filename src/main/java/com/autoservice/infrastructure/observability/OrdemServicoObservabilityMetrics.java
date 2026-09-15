package com.autoservice.infrastructure.observability;

import io.micrometer.core.instrument.MeterRegistry;

import java.util.Objects;

public final class OrdemServicoObservabilityMetrics {

    static final String METRIC_ERRO = "autoservice.ordem_servico.erro";

    private final MeterRegistry meterRegistry;

    public OrdemServicoObservabilityMetrics(final MeterRegistry meterRegistry) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);
    }

    public void registrarErro(final String path, final String tipo) {
        if (path == null || !path.contains("/ordens-servico")) {
            return;
        }

        meterRegistry.counter(METRIC_ERRO, "tipo", tipo).increment();
    }
}
