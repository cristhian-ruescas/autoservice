package com.autoservice.infrastructure.ordemservico.notificacao;

import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusNotificacao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.autoservice.support.NotificacaoTestFixtures.OCORRIDO_EM;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrdemServicoStatusClienteNotifierCompositeTest {

    @Mock
    private JavaMailOrdemServicoStatusClienteNotifier emailNotifier;

    @Mock
    private WebhookOrdemServicoStatusClienteNotifier webhookNotifier;

    @Mock
    private SimuladaOrdemServicoStatusClienteNotifier simuladaNotifier;

    @Test
    void deveDelegarParaTodosOsNotificadores() {
        final var composite = new OrdemServicoStatusClienteNotifierComposite(
                emailNotifier,
                webhookNotifier,
                simuladaNotifier
        );

        final var notificacao = new OrdemServicoStatusNotificacao(
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

        composite.notificar(notificacao);

        verify(emailNotifier).notificar(notificacao);
        verify(webhookNotifier).notificar(notificacao);
        verify(simuladaNotifier).notificar(notificacao);
    }
}
