package com.autoservice.presentation.dto.estoque;

import com.autoservice.application.estoque.localizacao.AtualizarLocalizacaoEstoqueOutput;

public record AtualizarLocalizacaoEstoqueResponse(
        String id,
        Integer quantidadeDisponivel,
        Integer quantidadeMinima,
        String localizacao
) {
    public static AtualizarLocalizacaoEstoqueResponse from(final AtualizarLocalizacaoEstoqueOutput output) {
        return new AtualizarLocalizacaoEstoqueResponse(
                output.id(),
                output.quantidadeDisponivel(),
                output.quantidadeMinima(),
                output.localizacao()
        );
    }
}
