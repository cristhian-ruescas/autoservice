package com.autoservice.presentation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdatePartRequest(
        @NotBlank String nome,
        @NotNull @Min(0) Integer quantidade,
        @NotNull @DecimalMin("0.0") BigDecimal precoUnitario
) {
}

