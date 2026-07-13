package com.autoservice.infrastructure.ordemservico.notificacao;

import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusNotificacao;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class NotificacaoSimuladaStore {

    private static final int LIMITE_PADRAO = 100;

    private final Deque<OrdemServicoStatusNotificacao> notificacoes = new ConcurrentLinkedDeque<>();

    public void registrar(final OrdemServicoStatusNotificacao notificacao) {
        this.notificacoes.addFirst(notificacao);
        while (this.notificacoes.size() > LIMITE_PADRAO) {
            this.notificacoes.removeLast();
        }
    }

    public List<OrdemServicoStatusNotificacao> listar(final int limite) {
        final int limiteEfetivo = Math.max(1, Math.min(limite, LIMITE_PADRAO));
        final var resultado = new ArrayList<OrdemServicoStatusNotificacao>(limiteEfetivo);

        for (final var notificacao : this.notificacoes) {
            resultado.add(notificacao);
            if (resultado.size() >= limiteEfetivo) {
                break;
            }
        }

        return Collections.unmodifiableList(resultado);
    }

    public void limpar() {
        this.notificacoes.clear();
    }
}
