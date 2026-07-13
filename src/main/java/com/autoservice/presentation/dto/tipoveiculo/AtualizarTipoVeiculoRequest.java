package com.autoservice.presentation.dto.tipoveiculo;

public record AtualizarTipoVeiculoRequest(
        String marca,
        String modelo,
        Integer ano
) {
}
