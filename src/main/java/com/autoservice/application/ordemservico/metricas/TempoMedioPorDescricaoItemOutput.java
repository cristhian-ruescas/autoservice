package com.autoservice.application.ordemservico.metricas;

public record TempoMedioPorDescricaoItemOutput(
        String descricaoItemServico,
        Double tempoMedioExecucaoSegundos,
        Double tempoMedioExecucaoHoras,
        Long quantidadeOrdensConsideradas
) {
}
