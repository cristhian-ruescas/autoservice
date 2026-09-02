package com.autoservice.presentation.dto.peca;

import java.math.BigDecimal;
import java.util.UUID;

public record AtualizarPecaRequest(
        String descricao,
        String codigo,
        String marca,
        BigDecimal valorUnitario,
        UUID tipoVeiculoId
) {
}
