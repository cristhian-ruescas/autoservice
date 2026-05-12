package com.autoservice.application.tipoveiculo.delete;

import java.util.UUID;

public record RemoverTipoVeiculoCommand(
        UUID tipoVeiculoId
) {

    public static RemoverTipoVeiculoCommand with(final UUID tipoVeiculoId) {
        return new RemoverTipoVeiculoCommand(tipoVeiculoId);
    }
}
