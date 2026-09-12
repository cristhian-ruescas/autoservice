package com.autoservice.application.ordemservico.status;

import com.autoservice.domain.ordemservico.OrdemServico;

public record OrdemServicoStatusOutput(
        String id,
        String status
) {
    public static OrdemServicoStatusOutput from(final OrdemServico ordemServico) {
        return new OrdemServicoStatusOutput(
                ordemServico.getId().getValue(),
                ordemServico.getStatus().name()
        );
    }
}
