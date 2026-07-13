package com.autoservice.application.servico.create;

import com.autoservice.domain.servico.Servico;

import java.math.BigDecimal;

public record CadastrarServicoOutput(
        String id,
        String nome,
        String descricao,
        BigDecimal valorReferencia
) {
    public static CadastrarServicoOutput from(final Servico servico) {
        return new CadastrarServicoOutput(
                servico.getId().getValue(),
                servico.getNome(),
                servico.getDescricao(),
                servico.getValorReferencia()
        );
    }
}
