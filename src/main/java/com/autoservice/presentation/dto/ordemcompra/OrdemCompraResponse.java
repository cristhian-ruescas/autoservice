package com.autoservice.presentation.dto.ordemcompra;

import com.autoservice.application.ordemcompra.realizar.RealizarOrdemCompraOutput;

import java.time.LocalDate;

public record OrdemCompraResponse(
        String id,
        String status,
        LocalDate dataCompra
) {

    public static OrdemCompraResponse from(final RealizarOrdemCompraOutput output) {
        return new OrdemCompraResponse(
                output.id(),
                output.status(),
                output.dataCompra()
        );
    }
}
