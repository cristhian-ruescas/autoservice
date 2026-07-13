package com.autoservice.infrastructure.ordemservico.notificacao;

import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusNotificacao;
import org.junit.jupiter.api.Test;

import static com.autoservice.support.NotificacaoTestFixtures.OCORRIDO_EM;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SimuladaOrdemServicoStatusClienteNotifierTest {

    @Test
    void deveRegistrarQuandoHabilitada() {
        final var store = new NotificacaoSimuladaStore();
        final var notifier = new SimuladaOrdemServicoStatusClienteNotifier(store, true);
        final var notificacao = notificacao("EM_DIAGNOSTICO");

        notifier.notificar(notificacao);

        assertEquals(1, store.listar(10).size());
        assertEquals("EM_DIAGNOSTICO", store.listar(10).get(0).statusNovo());
    }

    @Test
    void naoDeveRegistrarQuandoDesabilitada() {
        final var store = new NotificacaoSimuladaStore();
        final var notifier = new SimuladaOrdemServicoStatusClienteNotifier(store, false);

        notifier.notificar(notificacao("EM_DIAGNOSTICO"));

        assertEquals(0, store.listar(10).size());
    }

    private static OrdemServicoStatusNotificacao notificacao(final String statusNovo) {
        return new OrdemServicoStatusNotificacao(
                "os-1",
                "RECEBIDO",
                statusNovo,
                statusNovo,
                "cliente@test.local",
                "Cliente",
                "ABC1D23",
                "http://localhost/andamento",
                OCORRIDO_EM
        );
    }
}
