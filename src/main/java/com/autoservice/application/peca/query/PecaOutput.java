package com.autoservice.application.peca.query;

import com.autoservice.domain.peca.Peca;

import java.math.BigDecimal;

public record PecaOutput(
        String id,
        String descricao,
        String codigo,
        String marca,
        BigDecimal valorUnitario,
        Integer quantidadeEstoque,
        String tipoVeiculoId
) {
    public static PecaOutput from(final Peca peca) {
        return from(peca, null);
    }

    public static PecaOutput from(final Peca peca, final Integer quantidadeEstoque) {
        return new PecaOutput(
                peca.getId().getValue(),
                peca.getDescricao(),
                peca.getCodigo(),
                peca.getMarca(),
                peca.getValorUnitario(),
                quantidadeEstoque,
                peca.getTipoVeiculoId() == null ? null : peca.getTipoVeiculoId().getValue()
        );
    }
}
