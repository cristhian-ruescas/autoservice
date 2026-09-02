package com.autoservice.application.ordemservico.delete;

import java.util.UUID;

public record RemoverOrdemServicoCommand(
        UUID ordemServicoId
) {

    public static RemoverOrdemServicoCommand with(final UUID ordemServicoId) {
        return new RemoverOrdemServicoCommand(ordemServicoId);
    }
}
