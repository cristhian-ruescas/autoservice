package com.autoservice.application.ordemservico.finalizacao;

import java.util.UUID;

public record FinalizarOrdemServicoCommand(
        UUID ordemServicoId
) {

    public static FinalizarOrdemServicoCommand with(final UUID ordemServicoId) {
        return new FinalizarOrdemServicoCommand(ordemServicoId);
    }
}
