package com.autoservice.infrastructure.ordemservico.notificacao;

import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusClienteNotifier;
import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusNotificacao;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Primary
@Component
public class OrdemServicoStatusClienteNotifierComposite implements OrdemServicoStatusClienteNotifier {

    private final List<OrdemServicoStatusClienteNotifier> notificadores;

    public OrdemServicoStatusClienteNotifierComposite(
            final JavaMailOrdemServicoStatusClienteNotifier emailNotifier,
            final WebhookOrdemServicoStatusClienteNotifier webhookNotifier,
            final SimuladaOrdemServicoStatusClienteNotifier simuladaNotifier
    ) {
        this.notificadores = List.of(emailNotifier, webhookNotifier, simuladaNotifier);
    }

    @Override
    public void notificar(final OrdemServicoStatusNotificacao notificacao) {
        for (final var notificador : this.notificadores) {
            notificador.notificar(notificacao);
        }
    }
}
