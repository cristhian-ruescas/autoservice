package com.autoservice.application.peca.create;

import com.autoservice.domain.peca.Peca;

import java.math.BigDecimal;

public record CadastrarPecaOutput(
        String id,
        String descricao,
        String codigo,
        String marca,
        BigDecimal valorUnitario,
        Integer quantidadeEstoque,
        String tipoVeiculoId
) {
    public static CadastrarPecaOutput from(final Peca peca, final Integer quantidadeEstoque) {
        return new CadastrarPecaOutput(
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
