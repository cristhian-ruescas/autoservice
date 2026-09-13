package com.autoservice.presentation.dto.integracao;

import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusNotificacao;

import java.time.Instant;

public record NotificacaoSimuladaResponse(
        String ordemServicoId,
        String statusAnterior,
        String statusNovo,
        String statusNovoDescricao,
        String clienteEmail,
        String clienteNome,
        String placa,
        String andamentoUrl,
        Instant ocorridoEm
) {

    public static NotificacaoSimuladaResponse from(final OrdemServicoStatusNotificacao notificacao) {
        return new NotificacaoSimuladaResponse(
                notificacao.ordemServicoId(),
                notificacao.statusAnterior(),
                notificacao.statusNovo(),
                notificacao.statusNovoDescricao(),
                notificacao.clienteEmail(),
                notificacao.clienteNome(),
                notificacao.placa(),
                notificacao.andamentoUrl(),
                notificacao.ocorridoEm()
        );
    }
}
