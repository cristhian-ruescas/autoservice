package com.autoservice.presentation.controller;

import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusNotificacao;
import com.autoservice.infrastructure.ordemservico.notificacao.NotificacaoSimuladaStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.autoservice.support.NotificacaoTestFixtures.OCORRIDO_EM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificacaoSimuladaControllerTest {

    private NotificacaoSimuladaStore store;
    private NotificacaoSimuladaController controller;

    @BeforeEach
    void setUp() {
        this.store = new NotificacaoSimuladaStore();
        this.controller = new NotificacaoSimuladaController(this.store);
    }

    @Test
    void deveListarNotificacoesSimuladas() {
        this.store.registrar(notificacao("os-1"));

        final var response = this.controller.listar(10);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("os-1", response.getBody().get(0).ordemServicoId());
    }

    @Test
    void deveLimparNotificacoesSimuladas() {
        this.store.registrar(notificacao("os-1"));

        final var response = this.controller.limpar();

        assertEquals(204, response.getStatusCode().value());
        assertTrue(this.store.listar(10).isEmpty());
    }

    private static OrdemServicoStatusNotificacao notificacao(final String osId) {
        return new OrdemServicoStatusNotificacao(
                osId,
                null,
                "RECEBIDO",
                "Recebido",
                "cliente@test.local",
                "Cliente",
                "ABC1D23",
                "http://localhost/andamento",
                OCORRIDO_EM
        );
    }
}
