package com.autoservice.application.ordemservico.aprovacao;

import java.util.UUID;

public record ReprovarOrdemServicoCommand(
        UUID ordemServicoId
) {

    public static ReprovarOrdemServicoCommand with(final UUID ordemServicoId) {
        return new ReprovarOrdemServicoCommand(ordemServicoId);
    }
}
