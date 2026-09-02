package com.autoservice.application.ordemservico.create;

import java.util.UUID;

public record CreateOrdemServicoCommand(
        UUID veiculoId,
        String relato
) {
    public static CreateOrdemServicoCommand with(final UUID veiculoId, final String relato) {
        return new CreateOrdemServicoCommand(veiculoId, relato);
    }
}
