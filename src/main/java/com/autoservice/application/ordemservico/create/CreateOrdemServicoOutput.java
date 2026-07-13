package com.autoservice.application.ordemservico.create;

import com.autoservice.domain.ordemservico.OrdemServico;

public record CreateOrdemServicoOutput(
        String id
) {
    public static CreateOrdemServicoOutput from(final String anId) {
        return new CreateOrdemServicoOutput(anId);
    }

    public static CreateOrdemServicoOutput from(final OrdemServico ordemServico) {
        return new CreateOrdemServicoOutput(ordemServico.getId().getValue());
    }
}
