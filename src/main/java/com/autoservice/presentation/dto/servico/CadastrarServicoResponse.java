package com.autoservice.presentation.dto.servico;

import com.autoservice.application.servico.create.CadastrarServicoOutput;

import java.math.BigDecimal;

public record CadastrarServicoResponse(
        String id,
        String nome,
        String descricao,
        BigDecimal valorReferencia
) {
    public static CadastrarServicoResponse from(final CadastrarServicoOutput output) {
        return new CadastrarServicoResponse(
                output.id(),
                output.nome(),
                output.descricao(),
                output.valorReferencia()
        );
    }
}
