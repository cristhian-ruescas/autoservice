package com.autoservice.application.peca.create;

import java.math.BigDecimal;
import java.util.UUID;

public record CadastrarPecaCommand(
        String descricao,
        String codigo,
        String marca,
        BigDecimal valorUnitario,
        Integer quantidadeEstoque,
        UUID tipoVeiculoId
) {
    public static CadastrarPecaCommand with(
            final String descricao,
            final String codigo,
            final String marca,
            final BigDecimal valorUnitario,
            final Integer quantidadeEstoque,
            final UUID tipoVeiculoId
    ) {
        return new CadastrarPecaCommand(
                descricao,
                codigo,
                marca,
                valorUnitario,
                quantidadeEstoque == null ? 0 : quantidadeEstoque,
                tipoVeiculoId
        );
    }
}
