package com.autoservice.application.veiculo.create;

import java.util.UUID;

public record CadastrarVeiculoCommand(
        UUID clienteId,
        String placa,
        String marca,
        String modelo,
        Integer ano,
        String cor,
        Integer kilometragem
) {

    public static CadastrarVeiculoCommand with(
            final UUID clienteId,
            final String placa,
            final String marca,
            final String modelo,
            final Integer ano,
            final String cor,
            final Integer kilometragem
    ) {
        return new CadastrarVeiculoCommand(
                clienteId,
                placa,
                marca,
                modelo,
                ano,
                cor,
                kilometragem
        );
    }
}
