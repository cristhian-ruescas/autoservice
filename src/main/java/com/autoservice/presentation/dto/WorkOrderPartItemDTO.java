package com.autoservice.presentation.dto;

import java.math.BigDecimal;

public record WorkOrderPartItemDTO(
        String partId,
        String name,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}

