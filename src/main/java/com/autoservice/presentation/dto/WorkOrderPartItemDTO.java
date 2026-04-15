package com.autoservice.presentation.dto;

import java.math.BigDecimal;

public record WorkOrderPartItemDTO(
        String pecaId,
        String nome,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
) {
}

