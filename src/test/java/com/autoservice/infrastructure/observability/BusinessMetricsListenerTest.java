package com.autoservice.infrastructure.observability;

import com.autoservice.domain.atendimento.events.OrdemServicoAtendimentoAbertaEvent;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.events.OrdemServicoStatusAlteradoEvent;
import com.autoservice.domain.veiculo.VeiculoID;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BusinessMetricsListenerTest {

    @Test
    void deveIncrementarContadorDeAberturaDeOs() {
        final var meterRegistry = new SimpleMeterRegistry();
        final var listener = new BusinessMetricsListener(meterRegistry);

        listener.on(new OrdemServicoAtendimentoAbertaEvent(OrdemServicoID.unique(), VeiculoID.unique()));

        assertEquals(
                1.0,
                meterRegistry.get("autoservice.service_orders.opened.total")
                        .tag("origin", "atendimento")
                        .counter()
                        .count()
        );
    }

    @Test
    void deveIncrementarContadorDeTransicaoDeStatus() {
        final var meterRegistry = new SimpleMeterRegistry();
        final var listener = new BusinessMetricsListener(meterRegistry);

        listener.on(new OrdemServicoStatusAlteradoEvent(
                OrdemServicoID.unique(),
                OrdemServicoStatus.RECEBIDO,
                OrdemServicoStatus.EM_DIAGNOSTICO
        ));

        assertEquals(
                1.0,
                meterRegistry.get("autoservice.service_orders.status_transitions.total")
                        .tag("from", "RECEBIDO")
                        .tag("to", "EM_DIAGNOSTICO")
                        .counter()
                        .count()
        );
    }
}
