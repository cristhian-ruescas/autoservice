package com.autoservice.application.servico.query;

import com.autoservice.domain.servico.Servico;

import java.math.BigDecimal;

public record ServicoOutput(
        String id,
        String nome,
        String descricao,
        BigDecimal valorReferencia
) {
    public static ServicoOutput from(final Servico servico) {
        return new ServicoOutput(
                servico.getId().getValue(),
                servico.getNome(),
                servico.getDescricao(),
                servico.getValorReferencia()
        );
    }
}
