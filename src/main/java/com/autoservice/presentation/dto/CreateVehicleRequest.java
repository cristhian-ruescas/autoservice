package com.autoservice.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateVehicleRequest(
        @NotBlank String plate,
        @NotBlank String brand,
        @NotBlank String model,
        @NotNull Integer year,
        @NotBlank String customerId
) {
}

