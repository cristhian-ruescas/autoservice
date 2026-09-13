package com.autoservice.application.ordemservico.itemservico;

import com.autoservice.domain.itemservico.enums.ItemServicoTipo;

import java.math.BigDecimal;
import java.util.UUID;

public record AtualizarItemServicoCommand(
        UUID ordemServicoId,
        UUID itemServicoId,
        ItemServicoTipo tipo,
        String descricao,
        UUID pecaId,
        Integer quantidade,
        BigDecimal valorUnitario
) {

    public static AtualizarItemServicoCommand with(
            final UUID ordemServicoId,
            final UUID itemServicoId,
            final ItemServicoTipo tipo,
            final String descricao,
            final UUID pecaId,
            final Integer quantidade,
            final BigDecimal valorUnitario
    ) {
        return new AtualizarItemServicoCommand(
                ordemServicoId,
                itemServicoId,
                tipo,
                descricao,
                pecaId,
                quantidade,
                valorUnitario
        );
    }
}
