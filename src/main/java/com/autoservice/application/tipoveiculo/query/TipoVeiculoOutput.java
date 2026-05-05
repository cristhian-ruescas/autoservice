package com.autoservice.application.tipoveiculo.query;

import com.autoservice.domain.tipoveiculo.TipoVeiculo;

public record TipoVeiculoOutput(
        String id,
        String marca,
        String modelo,
        Integer ano
) {

    public static TipoVeiculoOutput from(final TipoVeiculo tipoVeiculo) {
        return new TipoVeiculoOutput(
                tipoVeiculo.getId().getValue(),
                tipoVeiculo.getMarca().getValue(),
                tipoVeiculo.getModelo().getValue(),
                tipoVeiculo.getAno().getValue()
        );
    }
}
