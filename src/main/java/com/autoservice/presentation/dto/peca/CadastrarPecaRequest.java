package com.autoservice.presentation.dto.peca;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CadastrarPecaRequest(
        @NotBlank String descricao,
        @NotBlank String codigo,
        @NotBlank String marca,
        @NotNull BigDecimal valorUnitario,
        @NotNull @Min(0) Integer quantidadeEstoque,
        UUID tipoVeiculoId
) {
}
