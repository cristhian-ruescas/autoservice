package com.autoservice.application.peca.query;

import com.autoservice.domain.peca.Peca;

import java.math.BigDecimal;

public record PecaOutput(
        String id,
        String descricao,
        String codigo,
        String marca,
        BigDecimal valorUnitario,
        String tipoVeiculoId
) {
    public static PecaOutput from(final Peca peca) {
        return new PecaOutput(
                peca.getId().getValue(),
                peca.getDescricao(),
                peca.getCodigo(),
                peca.getMarca(),
                peca.getValorUnitario(),
                peca.getTipoVeiculoId() == null ? null : peca.getTipoVeiculoId().getValue()
        );
    }
}
