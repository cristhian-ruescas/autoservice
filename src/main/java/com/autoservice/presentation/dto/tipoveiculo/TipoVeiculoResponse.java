package com.autoservice.presentation.dto.tipoveiculo;

import com.autoservice.application.tipoveiculo.create.CadastrarTipoVeiculoOutput;

public record TipoVeiculoResponse(
        String id,
        String marca,
        String modelo,
        Integer ano
) {

    public static TipoVeiculoResponse from(final CadastrarTipoVeiculoOutput output) {
        return new TipoVeiculoResponse(
                output.id(),
                output.marca(),
                output.modelo(),
                output.ano()
        );
    }
}
