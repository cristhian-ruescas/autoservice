package com.autoservice.application.servico.update;

import java.math.BigDecimal;
import java.util.UUID;

public record AtualizarServicoCommand(
        UUID servicoId,
        String nome,
        String descricao,
        BigDecimal valorReferencia
) {
    public static AtualizarServicoCommand with(
            final UUID servicoId,
            final String nome,
            final String descricao,
            final BigDecimal valorReferencia
    ) {
        return new AtualizarServicoCommand(servicoId, nome, descricao, valorReferencia);
    }
}
