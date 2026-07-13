package com.autoservice.application.ordemservico.entrega;

import java.util.UUID;

public record EntregarOrdemServicoCommand(
        UUID ordemServicoId
) {

    public static EntregarOrdemServicoCommand with(final UUID ordemServicoId) {
        return new EntregarOrdemServicoCommand(ordemServicoId);
    }
}
