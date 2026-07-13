package com.autoservice.domain.itemservico;

import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.peca.PecaID;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ItemServicoPecaAggregator {

    private ItemServicoPecaAggregator() {
    }

    public static Map<PecaID, Integer> quantidadesPorPeca(final List<ItemServico> itens) {
        return itens.stream()
                .filter(item -> item.getPecaId() != null)
                .collect(Collectors.groupingBy(
                        ItemServico::getPecaId,
                        Collectors.summingInt(ItemServico::getQuantidade)
                ));
    }

    public static Map<PecaID, Integer> quantidadesPorPeca(
            final ItemServicoGateway itemServicoGateway,
            final OrdemServicoID ordemServicoId
    ) {
        return quantidadesPorPeca(itemServicoGateway.findByOrdemServicoId(ordemServicoId));
    }
}
