package com.autoservice.application.ordemservico.update;

import java.util.UUID;

public record AtualizarOrdemServicoCommand(
        UUID ordemServicoId,
        UUID veiculoId,
        String relato,
        Integer tempoPrevistoExecucaoDias,
        Integer tempoPrevistoExecucaoHoras
) {

    public static AtualizarOrdemServicoCommand with(
            final UUID ordemServicoId,
            final UUID veiculoId,
            final String relato,
            final Integer tempoPrevistoExecucaoDias,
            final Integer tempoPrevistoExecucaoHoras
    ) {
        return new AtualizarOrdemServicoCommand(
                ordemServicoId,
                veiculoId,
                relato,
                tempoPrevistoExecucaoDias,
                tempoPrevistoExecucaoHoras
        );
    }
}
