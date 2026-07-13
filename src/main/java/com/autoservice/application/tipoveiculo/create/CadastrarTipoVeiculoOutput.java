package com.autoservice.application.tipoveiculo.create;

import com.autoservice.domain.tipoveiculo.TipoVeiculo;

public record CadastrarTipoVeiculoOutput(
        String id,
        String marca,
        String modelo,
        Integer ano
) {

    public static CadastrarTipoVeiculoOutput from(final TipoVeiculo tipoVeiculo) {
        return new CadastrarTipoVeiculoOutput(
                tipoVeiculo.getId().getValue(),
                tipoVeiculo.getMarca().getValue(),
                tipoVeiculo.getModelo().getValue(),
                tipoVeiculo.getAno().getValue()
        );
    }
}
