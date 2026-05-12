package com.autoservice.infrastructure.ordemservico.mail;

import com.autoservice.application.ordemservico.detail.DetailOrdemServicoOutput;
import com.autoservice.application.ordemservico.orcamento.OrcamentoEmailSender;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.Error;
import jakarta.mail.MessagingException;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class JavaMailOrcamentoEmailSender implements OrcamentoEmailSender {

    private final JavaMailSender mailSender;
    private final String from;
    private final String appBaseUrl;

    public JavaMailOrcamentoEmailSender(
            final JavaMailSender mailSender,
            @Value("${autoservice.mail.from}") final String from,
            @Value("${autoservice.app.base-url}") final String appBaseUrl
    ) {
        this.mailSender = mailSender;
        this.from = from;
        this.appBaseUrl = appBaseUrl;
    }

    @Override
    public void send(final DetailOrdemServicoOutput ordemServico, final byte[] pdf) {
        final String email = ordemServico.cliente().email();

        if (email == null || email.isBlank()) {
            throw DomainException.with(new Error("Cliente não possui email para envio do orçamento"));
        }

        try {
            final var message = this.mailSender.createMimeMessage();
            final var helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(this.from);
            helper.setTo(email);
            helper.setSubject("Orçamento da Ordem de Serviço " + ordemServico.ordemServicoId());
            helper.setText(plainTextBody(), htmlBody(ordemServico));
            helper.addAttachment(
                    "orcamento-" + ordemServico.ordemServicoId() + ".pdf",
                    new ByteArrayDataSource(pdf, "application/pdf")
            );

            this.mailSender.send(message);
        } catch (MessagingException | MailException exception) {
            throw new IllegalStateException("Não foi possível enviar o email do orçamento", exception);
        }
    }

    private String plainTextBody() {
        return """
                Olá,
                
                Segue em anexo o orçamento da sua ordem de serviço.
                
                Para aprovar ou reprovar o orçamento, abra este email em um cliente com suporte a HTML.
                
                AutoService
                """;
    }

    private String htmlBody(final DetailOrdemServicoOutput ordemServico) {
        final String aprovarUrl = approvalUrl(ordemServico.ordemServicoId(), "aprovar");
        final String reprovarUrl = approvalUrl(ordemServico.ordemServicoId(), "reprovar");

        return """
                <!doctype html>
                <html>
                <body style="font-family: Arial, sans-serif; color: #222; line-height: 1.45;">
                    <p>Olá,</p>
                    <p>Segue em anexo o orçamento da sua ordem de serviço.</p>
                    <p>Você pode aprovar ou reprovar o orçamento pelos botões abaixo:</p>
                    <p style="margin: 24px 0;">
                        <a href="%s" style="display: inline-block; padding: 12px 18px; background: #1f7a3f; color: #ffffff; text-decoration: none; border-radius: 4px; font-weight: bold;">Aprovar orçamento</a>
                        <a href="%s" style="display: inline-block; padding: 12px 18px; background: #b3261e; color: #ffffff; text-decoration: none; border-radius: 4px; font-weight: bold; margin-left: 8px;">Reprovar orçamento</a>
                    </p>
                    <p>AutoService</p>
                </body>
                </html>
                """.formatted(aprovarUrl, reprovarUrl);
    }

    private String approvalUrl(final String ordemServicoId, final String action) {
        return normalizedAppBaseUrl()
                + "/ordens-servico/"
                + ordemServicoId
                + "/aprovacao/"
                + action;
    }

    private String normalizedAppBaseUrl() {
        if (this.appBaseUrl.endsWith("/")) {
            return this.appBaseUrl.substring(0, this.appBaseUrl.length() - 1);
        }

        return this.appBaseUrl;
    }
}
