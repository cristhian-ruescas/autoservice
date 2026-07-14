package com.autoservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "ErrorResponse", description = "Erro de validação (400), domínio (422) ou interno (500).")
public record ErrorResponse(
        @Schema(example = "422")
        int status,
        @Schema(example = "Peça da ordem de serviço não possui estoque vinculado")
        String message,
        @Schema(example = "/ordens-servico/...")
        String path,
        LocalDateTime timestamp,
        List<FieldError> errors
) {
    @Schema(name = "ErrorField")
    public record FieldError(
            String field,
            String message
    ) {
    }
}
