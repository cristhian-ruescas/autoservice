package com.autoservice.application.ordemcompra.realizar;

import com.autoservice.domain.ordemcompra.OrdemCompra;

import java.time.LocalDate;

public record RealizarOrdemCompraOutput(
        String id,
        String status,
        LocalDate dataCompra
) {

    public static RealizarOrdemCompraOutput from(final OrdemCompra ordemCompra) {
        return new RealizarOrdemCompraOutput(
                ordemCompra.getId().getValue(),
                ordemCompra.getStatus().name(),
                ordemCompra.getDataCompra().getValue()
        );
    }
}
