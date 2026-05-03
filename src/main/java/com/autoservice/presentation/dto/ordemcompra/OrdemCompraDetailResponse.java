package com.autoservice.presentation.dto.ordemcompra;

import com.autoservice.application.ordemcompra.query.OrdemCompraOutput;

import java.time.LocalDate;
import java.util.List;

public record OrdemCompraDetailResponse(
        String id,
        String status,
        LocalDate dataCompra,
        List<ItemResponse> itens
) {
    public static OrdemCompraDetailResponse from(final OrdemCompraOutput output) {
        return new OrdemCompraDetailResponse(
                output.id(),
                output.status(),
                output.dataCompra(),
                output.itens().stream().map(ItemResponse::from).toList()
        );
    }

    public record ItemResponse(
            String id,
            String pecaId,
            String codigo,
            String descricao,
            Integer quantidade
    ) {
        public static ItemResponse from(final OrdemCompraOutput.ItemOutput output) {
            return new ItemResponse(
                    output.id(),
                    output.pecaId(),
                    output.codigo(),
                    output.descricao(),
                    output.quantidade()
            );
        }
    }
}
