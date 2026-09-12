package com.autoservice.presentation.dto.ordemservico;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;

public record FinalizarDiagnosticoRequest(
        @PositiveOrZero Integer tempoPrevistoExecucaoDias,
        @PositiveOrZero @Max(23) Integer tempoPrevistoExecucaoHoras
) {
}
