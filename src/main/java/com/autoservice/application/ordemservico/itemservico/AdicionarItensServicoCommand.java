package com.autoservice.application.ordemservico.itemservico;

import java.util.List;
import java.util.UUID;

public record AdicionarItensServicoCommand(
        UUID ordemServicoId,
        List<AdicionarItemServicoCommand> itens
) {
    public static AdicionarItensServicoCommand with(
            final UUID ordemServicoId,
            final List<AdicionarItemServicoCommand> itens
    ) {
        return new AdicionarItensServicoCommand(ordemServicoId, itens);
    }
}
