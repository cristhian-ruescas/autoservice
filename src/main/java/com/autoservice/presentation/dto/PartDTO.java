package com.autoservice.presentation.dto;

import java.math.BigDecimal;

public record PartDTO(
        String id,
        String nome,
        Integer quantidade,
        BigDecimal precoUnitario
) {
}

