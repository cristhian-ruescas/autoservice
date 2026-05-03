package com.autoservice.presentation.dto.ordemservico;

import com.autoservice.application.ordemservico.itemservico.AdicionarItemServicoOutput;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AdicionarItemServicoResponse(
        String id,
        String ordemServicoId,
        String tipo,
        String descricao,
        String pecaId,
        Integer quantidade,
        BigDecimal valorUnitario,
        BigDecimal valorTotal
) {
    public static AdicionarItemServicoResponse from(final AdicionarItemServicoOutput output) {
        return new AdicionarItemServicoResponse(
                output.id(),
                output.ordemServicoId(),
                output.tipo(),
                output.descricao(),
                output.pecaId(),
                output.quantidade(),
                output.valorUnitario(),
                output.valorTotal()
        );
    }
}
