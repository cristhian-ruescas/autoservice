package com.autoservice.application.ordemservico.notificacao;

import java.time.Instant;

public record OrdemServicoStatusNotificacao(
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
}
