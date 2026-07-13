package com.autoservice.application.veiculo.delete;

import java.util.UUID;

public record RemoverVeiculoCommand(
        UUID veiculoId
) {

    public static RemoverVeiculoCommand with(final UUID veiculoId) {
        return new RemoverVeiculoCommand(veiculoId);
    }
}
