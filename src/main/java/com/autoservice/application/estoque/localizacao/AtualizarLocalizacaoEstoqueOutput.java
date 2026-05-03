package com.autoservice.application.estoque.localizacao;

import com.autoservice.domain.estoque.Estoque;

public record AtualizarLocalizacaoEstoqueOutput(
        String id,
        Integer quantidadeDisponivel,
        Integer quantidadeMinima,
        String localizacao
) {
    public static AtualizarLocalizacaoEstoqueOutput from(final Estoque estoque) {
        return new AtualizarLocalizacaoEstoqueOutput(
                estoque.getId().getValue(),
                estoque.getQuantidadeDisponivel(),
                estoque.getQuantidadeMinima(),
                estoque.getLocalizacao()
        );
    }
}
