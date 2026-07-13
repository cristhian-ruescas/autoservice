package com.autoservice.application.tipoveiculo.create;

public record CadastrarTipoVeiculoCommand(
        String marca,
        String modelo,
        Integer ano
) {

    public static CadastrarTipoVeiculoCommand with(
            final String marca,
            final String modelo,
            final Integer ano
    ) {
        return new CadastrarTipoVeiculoCommand(marca, modelo, ano);
    }
}
