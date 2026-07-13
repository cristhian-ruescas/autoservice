package com.autoservice.infrastructure.ordemservico.notificacao;

import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusNotificacao;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static com.autoservice.support.NotificacaoTestFixtures.OCORRIDO_EM;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class WebhookOrdemServicoStatusClienteNotifierTest {

    @Test
    void naoDeveEnviarQuandoUrlNaoConfigurada() {
        final var notifier = new WebhookOrdemServicoStatusClienteNotifier(new ObjectMapper(), "");

        assertDoesNotThrow(() -> notifier.notificar(notificacao()));
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
