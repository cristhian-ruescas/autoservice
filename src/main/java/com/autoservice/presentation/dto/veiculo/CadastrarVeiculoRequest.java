package com.autoservice.presentation.dto.veiculo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CadastrarVeiculoRequest(
        @NotNull UUID clienteId,
        @NotBlank String placa,
        @NotBlank String marca,
        @NotBlank String modelo,
        @NotNull @Min(1900) Integer ano,
        @NotBlank String cor,
        @NotNull @Min(0) Integer kilometragem
) {
}
