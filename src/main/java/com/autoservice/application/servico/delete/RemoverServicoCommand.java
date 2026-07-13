package com.autoservice.application.servico.delete;

import java.util.UUID;

public record RemoverServicoCommand(UUID servicoId) {

    public static RemoverServicoCommand with(final UUID servicoId) {
        return new RemoverServicoCommand(servicoId);
    }
}
