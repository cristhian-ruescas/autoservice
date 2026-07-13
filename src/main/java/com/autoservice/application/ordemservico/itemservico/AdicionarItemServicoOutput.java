package com.autoservice.application.ordemservico.itemservico;

import com.autoservice.domain.itemservico.ItemServico;

import java.math.BigDecimal;

public record AdicionarItemServicoOutput(
        String id,
        String ordemServicoId,
        String tipo,
        String descricao,
        String pecaId,
        Integer quantidade,
        BigDecimal valorUnitario,
        BigDecimal valorTotal
) {
    public static AdicionarItemServicoOutput from(final ItemServico itemServico) {
        return new AdicionarItemServicoOutput(
                itemServico.getId().getValue(),
                itemServico.getOrdemServicoId().getValue(),
                itemServico.getTipo().name(),
                itemServico.getDescricao(),
                itemServico.getPecaId() == null ? null : itemServico.getPecaId().getValue(),
                itemServico.getQuantidade(),
                itemServico.getValorUnitario(),
                itemServico.getValorTotal()
        );
    }
}
