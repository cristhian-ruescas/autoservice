package com.autoservice.presentation.dto.servico;

import java.math.BigDecimal;

public record AtualizarServicoRequest(
        String nome,
        String descricao,
        BigDecimal valorReferencia
) {
}
