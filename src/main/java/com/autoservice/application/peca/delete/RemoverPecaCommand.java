package com.autoservice.application.peca.delete;

import java.util.UUID;

public record RemoverPecaCommand(
        UUID pecaId
) {

    public static RemoverPecaCommand with(final UUID pecaId) {
        return new RemoverPecaCommand(pecaId);
    }
}
