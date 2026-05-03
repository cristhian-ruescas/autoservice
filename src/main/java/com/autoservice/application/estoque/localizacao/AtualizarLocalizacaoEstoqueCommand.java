package com.autoservice.application.estoque.localizacao;

import java.util.UUID;

public record AtualizarLocalizacaoEstoqueCommand(
        UUID id,
        String localizacao
) {
    public static AtualizarLocalizacaoEstoqueCommand with(final UUID id, final String localizacao) {
        return new AtualizarLocalizacaoEstoqueCommand(id, localizacao);
    }
}
