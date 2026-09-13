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
    private final OrdemServicoDailyVolumeQuery dailyVolumeQuery;
    private final AtomicLong volumeDiario = new AtomicLong();

    public OrdemServicoMetricsPublisher(
            final MeterRegistry meterRegistry,
            final OrdemServicoDailyVolumeQuery dailyVolumeQuery
    ) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);
        this.dailyVolumeQuery = Objects.requireNonNull(dailyVolumeQuery);
    }

    @PostConstruct
    void registrarGauge() {
        Gauge.builder(METRIC_VOLUME_DIARIO, volumeDiario, AtomicLong::get)
                .description("Quantidade de ordens de servico abertas no dia corrente")
                .register(meterRegistry);
        publicarVolumeDiario();
    }

    @Scheduled(fixedRateString = "${autoservice.metrics.publish-interval-ms:60000}")
    void publicarVolumeDiario() {
        volumeDiario.set(dailyVolumeQuery.contarAbertasHoje());
    }
}
