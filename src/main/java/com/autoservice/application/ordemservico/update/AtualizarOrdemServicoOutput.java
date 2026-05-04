package com.autoservice.application.ordemservico.update;

import com.autoservice.domain.ordemservico.OrdemServico;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AtualizarOrdemServicoOutput(
        String id,
        String veiculoId,
        String status,
        LocalDate dataCriacao,
        String relato,
        Integer tempoPrevistoExecucaoDias,
        Integer tempoPrevistoExecucaoHoras,
        LocalDateTime iniciadoEm,
        LocalDateTime finalizadoEm
) {

    public static AtualizarOrdemServicoOutput from(final OrdemServico ordemServico) {
        return new AtualizarOrdemServicoOutput(
                ordemServico.getId().getValue(),
                ordemServico.getVeiculoId().getValue(),
                ordemServico.getStatus().name(),
                ordemServico.getDataCriacao().getValue(),
                ordemServico.getRelato(),
                ordemServico.getTempoPrevistoExecucaoDias(),
                ordemServico.getTempoPrevistoExecucaoHoras(),
                ordemServico.getIniciadoEm(),
                ordemServico.getFinalizadoEm()
        );
    }
}
