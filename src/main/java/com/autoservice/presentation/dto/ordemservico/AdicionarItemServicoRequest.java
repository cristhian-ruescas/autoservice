package com.autoservice.presentation.dto.ordemservico;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.UUID;

public record AdicionarItemServicoRequest(
        @NotBlank String tipo,
        String descricao,
        UUID pecaId,
        Integer quantidade,
        BigDecimal valorUnitario
) {
}
