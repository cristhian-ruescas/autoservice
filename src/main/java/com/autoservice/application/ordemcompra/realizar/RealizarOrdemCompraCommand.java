package com.autoservice.application.ordemcompra.realizar;

import java.util.UUID;

public record RealizarOrdemCompraCommand(
        UUID ordemCompraId
) {

    public static RealizarOrdemCompraCommand with(final UUID ordemCompraId) {
        return new RealizarOrdemCompraCommand(ordemCompraId);
    }
}
