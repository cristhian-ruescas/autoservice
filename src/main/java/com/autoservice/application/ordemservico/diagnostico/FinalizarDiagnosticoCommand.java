package com.autoservice.application.ordemservico.diagnostico;

import java.util.UUID;

public record FinalizarDiagnosticoCommand(
        UUID ordemServicoId,
        Integer tempoPrevistoExecucaoDias,
        Integer tempoPrevistoExecucaoHoras
) {
    public static FinalizarDiagnosticoCommand with(
            final UUID ordemServicoId,
            final Integer tempoPrevistoExecucaoDias,
            final Integer tempoPrevistoExecucaoHoras
    ) {
        return new FinalizarDiagnosticoCommand(
                ordemServicoId,
                tempoPrevistoExecucaoDias,
                tempoPrevistoExecucaoHoras
        );
    }
}
