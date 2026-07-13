package com.autoservice.application.ordemservico.itemservico;

import com.autoservice.domain.itemservico.enums.ItemServicoTipo;

import java.math.BigDecimal;
import java.util.UUID;

public record AdicionarItemServicoCommand(
        UUID ordemServicoId,
        ItemServicoTipo tipo,
        String descricao,
        UUID pecaId,
        Integer quantidade,
        BigDecimal valorUnitario
) {
    public static AdicionarItemServicoCommand with(
            final UUID ordemServicoId,
            final ItemServicoTipo tipo,
            final String descricao,
            final UUID pecaId,
            final Integer quantidade,
            final BigDecimal valorUnitario
    ) {
        return new AdicionarItemServicoCommand(
                ordemServicoId,
                tipo,
                descricao,
                pecaId,
                quantidade,
                valorUnitario
        );
    }
}
