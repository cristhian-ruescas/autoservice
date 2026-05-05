package com.autoservice.presentation.dto.ordemservico;

import java.math.BigDecimal;
import java.util.UUID;

public record AtualizarItemServicoRequest(
        String tipo,
        String descricao,
        UUID pecaId,
        Integer quantidade,
        BigDecimal valorUnitario
) {
}
