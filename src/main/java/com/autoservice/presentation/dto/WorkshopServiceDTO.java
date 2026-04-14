package com.autoservice.presentation.dto;

import java.math.BigDecimal;

public record WorkshopServiceDTO(
        String id,
        String name,
        String description,
        BigDecimal basePrice
) {
}

