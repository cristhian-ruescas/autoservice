package com.autoservice.presentation.dto.ordemservico;

import com.autoservice.application.ordemservico.itemservico.AdicionarItensServicoOutput;

import java.math.BigDecimal;
import java.util.List;

public record AdicionarItensServicoResponse(
        List<AdicionarItemServicoResponse> itens,
        BigDecimal valorTotal
) {
    public static AdicionarItensServicoResponse from(final AdicionarItensServicoOutput output) {
        return new AdicionarItensServicoResponse(
                output.itens().stream()
                        .map(AdicionarItemServicoResponse::from)
                        .toList(),
                output.valorTotal()
        );
    }
}
