package com.autoservice.presentation.dto.estoque;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarLocalizacaoEstoqueRequest(
        @NotBlank @Size(max = 120) String localizacao
) {
}
