package com.autoservice.application.ordemservico.itemservico;

import java.math.BigDecimal;
import java.util.List;

public record AdicionarItensServicoOutput(
        List<AdicionarItemServicoOutput> itens,
        BigDecimal valorTotal
) {
    public static AdicionarItensServicoOutput from(final List<AdicionarItemServicoOutput> itens) {
        final BigDecimal valorTotal = itens.stream()
                .map(AdicionarItemServicoOutput::valorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AdicionarItensServicoOutput(itens, valorTotal);
    }
}
