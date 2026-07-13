package com.autoservice.presentation.dto.servico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CadastrarServicoRequest(
        @NotBlank @Size(max = 120) String nome,
        @Size(max = 500) String descricao,
        BigDecimal valorReferencia
) {
}
