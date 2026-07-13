package com.autoservice.application.ordemservico.metricas;

import java.util.List;

public record TempoMedioExecucaoOutput(
        Double tempoMedioGlobalSegundos,
        Double tempoMedioGlobalHoras,
        List<TempoMedioPorDescricaoItemOutput> porDescricaoItemServico
) {
}
