package com.autoservice.presentation.dto.ordemservico;

import com.autoservice.application.ordemservico.status.OrdemServicoStatusOutput;

public record OrdemServicoStatusResponse(
        String id,
        String status
) {
    public static OrdemServicoStatusResponse from(final OrdemServicoStatusOutput output) {
        return new OrdemServicoStatusResponse(
                output.id(),
                output.status()
        );
    }
}
