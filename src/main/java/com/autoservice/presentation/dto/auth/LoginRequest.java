package com.autoservice.presentation.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest", description = "Credenciais para emissão do JWT.")
public record LoginRequest(
        @NotBlank
        @Schema(example = "admin@autoservice.local", description = "E-mail do usuário")
        String username,
        @NotBlank
        @Schema(example = "admin123", description = "Senha")
        String password
) {
}
