package com.autoservice.presentation.dto.peca;

import com.autoservice.application.peca.create.CadastrarPecaOutput;

import java.math.BigDecimal;

public record CadastrarPecaResponse(
        String id,
        String descricao,
        String codigo,
        String marca,
        BigDecimal valorUnitario,
        String tipoVeiculoId
) {
    public static CadastrarPecaResponse from(final CadastrarPecaOutput output) {
        return new CadastrarPecaResponse(
                output.id(),
                output.descricao(),
                output.codigo(),
                output.marca(),
                output.valorUnitario(),
                output.tipoVeiculoId()
        );
    }
}
