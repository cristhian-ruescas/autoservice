package com.autoservice.presentation.dto;

import java.math.BigDecimal;

public record WorkOrderServiceItemDTO(
        String serviceId,
        String name,
        BigDecimal price
) {
}

