package com.autoservice.presentation.dto.estoque;

import com.autoservice.application.estoque.query.EstoqueOutput;

public record EstoqueResponse(
        String id,
        Integer quantidadeDisponivel,
        Integer quantidadeMinima,
        String localizacao,
        PecaResponse peca
) {
    public static EstoqueResponse from(final EstoqueOutput output) {
        return new EstoqueResponse(
                output.id(),
                output.quantidadeDisponivel(),
                output.quantidadeMinima(),
                output.localizacao(),
                output.peca() == null ? null : PecaResponse.from(output.peca())
        );
    }

    public record PecaResponse(
            String id,
            String codigo,
            String descricao,
            String marca
    ) {
        public static PecaResponse from(final EstoqueOutput.PecaOutput output) {
            return new PecaResponse(
                    output.id(),
                    output.codigo(),
                    output.descricao(),
                    output.marca()
            );
        }
    }
}
