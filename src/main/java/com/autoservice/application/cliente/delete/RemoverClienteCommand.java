package com.autoservice.application.cliente.delete;

import java.util.UUID;

public record RemoverClienteCommand(
        UUID clienteId
) {

    public static RemoverClienteCommand with(final UUID clienteId) {
        return new RemoverClienteCommand(clienteId);
    }
}
