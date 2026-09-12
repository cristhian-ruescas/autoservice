package com.autoservice.infrastructure.ordemservico.notificacao;

import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusNotificacao;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static com.autoservice.support.NotificacaoTestFixtures.OCORRIDO_EM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JavaMailOrdemServicoStatusClienteNotifierTest {

    @Mock
    private JavaMailSender mailSender;

    @Test
    void deveEnviarEmailQuandoStatusPermitido() throws Exception {
        final var notifier = new JavaMailOrdemServicoStatusClienteNotifier(mailSender, "noreply@autoservice.local");
        final var session = jakarta.mail.Session.getInstance(new Properties());
        final var mimeMessage = new MimeMessage(session);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        notifier.notificar(notificacao("EM_DIAGNOSTICO", "cliente@test.local", "Maria"));

        verify(mailSender).send(mimeMessage);
        assertEquals("cliente@test.local", mimeMessage.getAllRecipients()[0].toString());
    }

    @Test
    void naoDeveEnviarEmailParaAguardandoAprovacao() {
        final var notifier = new JavaMailOrdemServicoStatusClienteNotifier(mailSender, "noreply@autoservice.local");

        notifier.notificar(notificacao("AGUARDANDO_APROVACAO", "cliente@test.local", "Maria"));

        verifyNoInteractions(mailSender);
    }

    @Test
    void naoDeveEnviarEmailSemDestinatario() {
        final var notifier = new JavaMailOrdemServicoStatusClienteNotifier(mailSender, "noreply@autoservice.local");

        notifier.notificar(notificacao("EM_EXECUCAO", " ", "Maria"));

        verifyNoInteractions(mailSender);
    }

    @Test
    void deveIgnorarFalhaDeEnvio() throws Exception {
        final var notifier = new JavaMailOrdemServicoStatusClienteNotifier(mailSender, "noreply@autoservice.local");
        final var session = jakarta.mail.Session.getInstance(new Properties());
        final var mimeMessage = new MimeMessage(session);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new org.springframework.mail.MailSendException("smtp indisponivel")).when(mailSender).send(mimeMessage);

        notifier.notificar(notificacao("FINALIZADA", "cliente@test.local", "Maria"));

        verify(mailSender).send(mimeMessage);
    }

    private static OrdemServicoStatusNotificacao notificacao(
            final String statusNovo,
            final String email,
            final String nome
    ) {
        return new OrdemServicoStatusNotificacao(
                "os-1",
                "EM_EXECUCAO",
                statusNovo,
                statusNovo,
                email,
                nome,
                "ABC1D23",
                "http://localhost/andamento",
                OCORRIDO_EM
        );
    }
}
