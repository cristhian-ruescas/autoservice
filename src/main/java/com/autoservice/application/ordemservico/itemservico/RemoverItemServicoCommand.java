package com.autoservice.application.ordemservico.itemservico;

import java.util.UUID;

public record RemoverItemServicoCommand(
        UUID ordemServicoId,
        UUID itemServicoId
) {

    public static RemoverItemServicoCommand with(
            final UUID ordemServicoId,
            final UUID itemServicoId
    ) {
        return new RemoverItemServicoCommand(ordemServicoId, itemServicoId);
    }
}
