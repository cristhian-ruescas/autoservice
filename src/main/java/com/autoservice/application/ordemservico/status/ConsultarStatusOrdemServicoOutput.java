package com.autoservice.application.ordemservico.status;

import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConsultarStatusOrdemServicoOutput(
        String ordemServicoId,
        String status,
        String descricaoStatus,
        LocalDate dataCriacao,
        LocalDateTime iniciadoEm,
        LocalDateTime finalizadoEm
) {

    public static ConsultarStatusOrdemServicoOutput from(final ListOrdemServicoOutput ordemServico) {
        final var status = OrdemServicoStatus.valueOf(ordemServico.status());

        return new ConsultarStatusOrdemServicoOutput(
                ordemServico.ordemServicoId(),
                status.name(),
                status.getDescricao(),
                ordemServico.dataCriacao(),
                ordemServico.iniciadoEm(),
                ordemServico.finalizadoEm()
        );
    }
}
