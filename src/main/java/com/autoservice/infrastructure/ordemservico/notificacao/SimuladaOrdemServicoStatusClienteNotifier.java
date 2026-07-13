package com.autoservice.infrastructure.ordemservico.notificacao;

import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusClienteNotifier;
import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusNotificacao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SimuladaOrdemServicoStatusClienteNotifier implements OrdemServicoStatusClienteNotifier {

    private final NotificacaoSimuladaStore notificacaoSimuladaStore;
    private final boolean habilitada;

    public SimuladaOrdemServicoStatusClienteNotifier(
            final NotificacaoSimuladaStore notificacaoSimuladaStore,
            @Value("${autoservice.notificacao.simulada.habilitada:true}") final boolean habilitada
    ) {
        this.notificacaoSimuladaStore = notificacaoSimuladaStore;
        this.habilitada = habilitada;
    }

    @Override
    public void notificar(final OrdemServicoStatusNotificacao notificacao) {
        if (!this.habilitada) {
            return;
        }

        this.notificacaoSimuladaStore.registrar(notificacao);
    }
}
