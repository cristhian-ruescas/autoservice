package com.autoservice.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateVehicleRequest(
        @NotBlank String placa,
        @NotBlank String marca,
        @NotBlank String modelo,
        @NotNull Integer ano,
        @NotBlank String clienteId
) {
}

