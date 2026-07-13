package com.autoservice.application.veiculo.update;

import java.util.UUID;

public record AtualizarVeiculoCommand(
        UUID veiculoId,
        String placa,
        String marca,
        String modelo,
        Integer ano,
        String cor,
        Integer kilometragem
) {

    public static AtualizarVeiculoCommand with(
            final UUID veiculoId,
            final String placa,
            final String marca,
            final String modelo,
            final Integer ano,
            final String cor,
            final Integer kilometragem
    ) {
        return new AtualizarVeiculoCommand(
                veiculoId,
                placa,
                marca,
                modelo,
                ano,
                cor,
                kilometragem
        );
    }
}
