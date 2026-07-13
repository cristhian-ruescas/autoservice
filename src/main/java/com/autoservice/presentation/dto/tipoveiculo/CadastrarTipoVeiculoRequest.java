package com.autoservice.presentation.dto.tipoveiculo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CadastrarTipoVeiculoRequest(
        @NotBlank String marca,
        @NotBlank String modelo,
        @NotNull @Min(1900) Integer ano
) {
}
