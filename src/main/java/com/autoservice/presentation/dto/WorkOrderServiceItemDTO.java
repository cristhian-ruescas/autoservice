package com.autoservice.presentation.dto;

import java.math.BigDecimal;

public record WorkOrderServiceItemDTO(
        String servicoId,
        String nome,
        BigDecimal preco
) {
}

