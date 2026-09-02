package com.autoservice.infrastructure.ordemservico.notificacao;

import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusNotificacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificacaoSimuladaStoreTest {

    private NotificacaoSimuladaStore store;

    @BeforeEach
    void setUp() {
        this.store = new NotificacaoSimuladaStore();
    }

    @Test
    void deveRegistrarEListarNotificacoes() {
        final var primeira = notificacao("os-1", "RECEBIDO");
        final var segunda = notificacao("os-2", "EM_DIAGNOSTICO");

        this.store.registrar(primeira);
        this.store.registrar(segunda);

        final List<OrdemServicoStatusNotificacao> notificacoes = this.store.listar(10);

        assertEquals(2, notificacoes.size());
        assertEquals("os-2", notificacoes.get(0).ordemServicoId());
        assertEquals("os-1", notificacoes.get(1).ordemServicoId());
    }

    @Test
    void deveLimparNotificacoes() {
        this.store.registrar(notificacao("os-1", "RECEBIDO"));

        this.store.limpar();

        assertTrue(this.store.listar(10).isEmpty());
    }

    private static OrdemServicoStatusNotificacao notificacao(final String osId, final String statusNovo) {
        return new OrdemServicoStatusNotificacao(
                osId,
                null,
                statusNovo,
                statusNovo,
                "cliente@test.local",
                "Cliente",
                "ABC1D23",
                "http://localhost/andamento",
                Instant.parse("2026-07-06T22:00:00Z")
        );
    }
}
