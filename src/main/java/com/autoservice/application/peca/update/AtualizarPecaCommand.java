package com.autoservice.application.peca.update;

import java.math.BigDecimal;
import java.util.UUID;

public record AtualizarPecaCommand(
        UUID pecaId,
        String descricao,
        String codigo,
        String marca,
        BigDecimal valorUnitario,
        UUID tipoVeiculoId
) {
    public static AtualizarPecaCommand with(
            final UUID pecaId,
            final String descricao,
            final String codigo,
            final String marca,
            final BigDecimal valorUnitario,
            final UUID tipoVeiculoId
    ) {
        return new AtualizarPecaCommand(
                pecaId,
                descricao,
                codigo,
                marca,
                valorUnitario,
                tipoVeiculoId
        );
    }
}
