package com.autoservice.presentation.dto;

import java.math.BigDecimal;

public record WorkshopServiceDTO(
        String id,
        String nome,
        String descricao,
        BigDecimal precoBase
) {
}

