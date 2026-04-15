package com.autoservice.presentation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateWorkshopServiceRequest(
        @NotBlank String nome,
        String descricao,
        @NotNull @DecimalMin("0.0") BigDecimal precoBase
) {
}

