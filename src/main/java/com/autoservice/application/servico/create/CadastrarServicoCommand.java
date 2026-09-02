package com.autoservice.application.servico.create;

import java.math.BigDecimal;

public record CadastrarServicoCommand(
        String nome,
        String descricao,
        BigDecimal valorReferencia
) {
    public static CadastrarServicoCommand with(
            final String nome,
            final String descricao,
            final BigDecimal valorReferencia
    ) {
        return new CadastrarServicoCommand(nome, descricao, valorReferencia);
    }
}
