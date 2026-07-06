package com.autoservice.infrastructure.ordemservico.notificacao;

import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusClienteNotifier;
import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusNotificacao;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class JavaMailOrdemServicoStatusClienteNotifier implements OrdemServicoStatusClienteNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaMailOrdemServicoStatusClienteNotifier.class);

    private final JavaMailSender mailSender;
    private final String from;

    public JavaMailOrdemServicoStatusClienteNotifier(
            final JavaMailSender mailSender,
            @Value("${autoservice.mail.from}") final String from
    ) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void notificar(final OrdemServicoStatusNotificacao notificacao) {
        if (OrdemServicoStatus.AGUARDANDO_APROVACAO.name().equals(notificacao.statusNovo())) {
            return;
        }

        final String email = notificacao.clienteEmail();
        if (email == null || email.isBlank()) {
            LOGGER.warn(
                    "Cliente sem e-mail; notificação de status da OS {} não enviada por e-mail",
                    notificacao.ordemServicoId()
            );
            return;
        }

        try {
            final var message = this.mailSender.createMimeMessage();
            final var helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(this.from);
            helper.setTo(email);
            helper.setSubject("Atualização da Ordem de Serviço " + notificacao.ordemServicoId());
            helper.setText(corpoTexto(notificacao), corpoHtml(notificacao));

            this.mailSender.send(message);
        } catch (MessagingException | MailException exception) {
            LOGGER.error(
                    "Falha ao enviar e-mail de status da OS {}",
                    notificacao.ordemServicoId(),
                    exception
            );
        }
    }

    private String corpoTexto(final OrdemServicoStatusNotificacao notificacao) {
        return """
                Olá%s,

                Sua ordem de serviço foi atualizada para: %s.

                Acompanhe o andamento em: %s

                AutoService
                """.formatted(
                nomeCliente(notificacao),
                notificacao.statusNovoDescricao(),
                notificacao.andamentoUrl()
        );
    }

    private String corpoHtml(final OrdemServicoStatusNotificacao notificacao) {
        return """
                <!doctype html>
                <html>
                <body style="font-family: Arial, sans-serif; color: #222; line-height: 1.45;">
                    <p>Olá%s,</p>
                    <p>Sua ordem de serviço foi atualizada para: <strong>%s</strong>.</p>
                    <p><a href="%s">Acompanhar andamento da ordem de serviço</a></p>
                    <p>AutoService</p>
                </body>
                </html>
                """.formatted(
                nomeCliente(notificacao),
                notificacao.statusNovoDescricao(),
                notificacao.andamentoUrl()
        );
    }

    private String nomeCliente(final OrdemServicoStatusNotificacao notificacao) {
        if (notificacao.clienteNome() == null || notificacao.clienteNome().isBlank()) {
            return "";
        }

        return " " + notificacao.clienteNome();
    }
}
