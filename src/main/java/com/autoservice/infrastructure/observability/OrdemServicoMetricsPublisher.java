package com.autoservice.infrastructure.observability;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Component
@EnableScheduling
public class OrdemServicoMetricsPublisher {

    static final String METRIC_VOLUME_DIARIO = "autoservice.ordem_servico.volume_diario";

    private final MeterRegistry meterRegistry;
    private final OrdemServicoVolumeDiarioQuery volumeDiarioQuery;
    private final AtomicLong volumeDiario = new AtomicLong();

    public OrdemServicoMetricsPublisher(
            final MeterRegistry meterRegistry,
            final OrdemServicoVolumeDiarioQuery volumeDiarioQuery
    ) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);
        this.volumeDiarioQuery = Objects.requireNonNull(volumeDiarioQuery);
    }

    @PostConstruct
    void registrarGauge() {
        Gauge.builder(METRIC_VOLUME_DIARIO, volumeDiario, AtomicLong::get)
                .description("Quantidade de ordens de servico abertas no dia corrente")
                .register(meterRegistry);
        try {
            publicarVolumeDiario();
        } catch (Exception ex) {
            // Schema/tabelas podem ainda nao existir no primeiro boot (ddl-auto update).
            volumeDiario.set(0);
        }
    }

    @Scheduled(fixedRateString = "${autoservice.metrics.publish-interval-ms:60000}")
    void publicarVolumeDiario() {
        try {
            volumeDiario.set(volumeDiarioQuery.contarAbertasHoje());
        } catch (Exception ex) {
            volumeDiario.set(0);
        }
    }
}
