package com.autoservice.presentation.dto.ordemservico;

import java.util.UUID;

public record AtualizarOrdemServicoRequest(
        UUID veiculoId,
        String relato,
        Integer tempoPrevistoExecucaoDias,
        Integer tempoPrevistoExecucaoHoras
) {
}
