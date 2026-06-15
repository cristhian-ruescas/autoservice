package com.autoservice.application.atendimento.create;

import com.autoservice.domain.itemservico.enums.ItemServicoTipo;

import java.math.BigDecimal;
import java.util.UUID;

public record AbrirAtendimentoItemCommand(
        ItemServicoTipo tipo,
        String descricao,
        UUID pecaId,
        Integer quantidade,
        BigDecimal valorUnitario
) {
    public static AbrirAtendimentoItemCommand with(
            final ItemServicoTipo tipo,
            final String descricao,
            final UUID pecaId,
            final Integer quantidade,
            final BigDecimal valorUnitario
    ) {
        return new AbrirAtendimentoItemCommand(tipo, descricao, pecaId, quantidade, valorUnitario);
    }
}
