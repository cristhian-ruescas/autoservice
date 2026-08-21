package com.autoservice.infrastructure.ordemservico.notificacao;

import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusNotificacao;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static com.autoservice.support.NotificacaoTestFixtures.OCORRIDO_EM;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WebhookOrdemServicoStatusClienteNotifierTest {

    @Test
    void naoDeveEnviarQuandoUrlNaoConfigurada() {
        final var meterRegistry = new SimpleMeterRegistry();
        final var notifier = new WebhookOrdemServicoStatusClienteNotifier(new ObjectMapper(), meterRegistry, "");

        assertDoesNotThrow(() -> notifier.notificar(notificacao()));
        assertEquals(
                1.0,
                meterRegistry.get("autoservice.notifications.webhook.skipped")
                        .tag("reason", "webhook_blank")
                        .counter()
                        .count()
        );
    }

    private static OrdemServicoStatusNotificacao notificacao() {
        return new OrdemServicoStatusNotificacao(
                "os-1",
                "RECEBIDO",
                "EM_DIAGNOSTICO",
                "Em Diagnóstico",
                "cliente@test.local",
                "Cliente",
                "ABC1D23",
                "http://localhost/andamento",
                OCORRIDO_EM
        );
    }
}
