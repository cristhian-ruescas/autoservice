package com.autoservice.application.ordemcompra.query;

import java.time.LocalDate;
import java.util.List;

public record OrdemCompraOutput(
        String id,
        String status,
        LocalDate dataCompra,
        List<ItemOutput> itens
) {
    public record ItemOutput(
            String id,
            String pecaId,
            String codigo,
            String descricao,
            Integer quantidade
    ) {
    }
}
