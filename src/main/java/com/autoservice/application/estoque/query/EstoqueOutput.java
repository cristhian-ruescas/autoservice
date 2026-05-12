package com.autoservice.application.estoque.query;

public record EstoqueOutput(
        String id,
        Integer quantidadeDisponivel,
        Integer quantidadeMinima,
        String localizacao,
        PecaOutput peca
) {
    public record PecaOutput(
            String id,
            String codigo,
            String descricao,
            String marca
    ) {
    }
}
