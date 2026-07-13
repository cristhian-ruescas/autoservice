package com.autoservice.presentation.dto.veiculo;

public record AtualizarVeiculoRequest(
        String placa,
        String marca,
        String modelo,
        Integer ano,
        String cor,
        Integer kilometragem
) {
}
