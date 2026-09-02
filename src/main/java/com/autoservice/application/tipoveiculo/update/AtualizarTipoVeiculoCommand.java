package com.autoservice.application.tipoveiculo.update;

import java.util.UUID;

public record AtualizarTipoVeiculoCommand(
        UUID tipoVeiculoId,
        String marca,
        String modelo,
        Integer ano
) {

    public static AtualizarTipoVeiculoCommand with(
            final UUID tipoVeiculoId,
            final String marca,
            final String modelo,
            final Integer ano
    ) {
        return new AtualizarTipoVeiculoCommand(
                tipoVeiculoId,
                marca,
                modelo,
                ano
        );
    }
}
