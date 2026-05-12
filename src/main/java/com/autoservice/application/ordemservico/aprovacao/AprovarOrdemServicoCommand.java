package com.autoservice.application.ordemservico.aprovacao;

import java.util.UUID;

public record AprovarOrdemServicoCommand(
        UUID ordemServicoId
) {

    public static AprovarOrdemServicoCommand with(final UUID ordemServicoId) {
        return new AprovarOrdemServicoCommand(ordemServicoId);
    }
}
