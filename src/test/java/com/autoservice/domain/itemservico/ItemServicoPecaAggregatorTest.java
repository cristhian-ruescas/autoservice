package com.autoservice.domain.itemservico;

import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.peca.PecaID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ItemServicoPecaAggregator")
class ItemServicoPecaAggregatorTest {

    @Test
    @DisplayName("Deve agrupar quantidades por peça")
    void deveAgruparQuantidadesPorPeca() {
        final var pecaId = PecaID.unique();
        final var outraPecaId = PecaID.unique();
        final var ordemServicoId = OrdemServicoID.unique();

        final var itens = List.of(
                ItemServico.newPeca(ordemServicoId, "Filtro", pecaId, 2, BigDecimal.valueOf(50.0)),
                ItemServico.newPeca(ordemServicoId, "Filtro", pecaId, 1, BigDecimal.valueOf(50.0)),
                ItemServico.newServico(ordemServicoId, "Troca de óleo", BigDecimal.valueOf(120.0)),
                ItemServico.newPeca(ordemServicoId, "Pastilha", outraPecaId, 4, BigDecimal.valueOf(80.0))
        );

        final var quantidades = ItemServicoPecaAggregator.quantidadesPorPeca(itens);

        assertEquals(2, quantidades.size());
        assertEquals(3, quantidades.get(pecaId));
        assertEquals(4, quantidades.get(outraPecaId));
    }

    @Test
    @DisplayName("Deve retornar mapa vazio quando não houver peças")
    void deveRetornarMapaVazioSemPecas() {
        final var quantidades = ItemServicoPecaAggregator.quantidadesPorPeca(List.of(
                ItemServico.newServico(OrdemServicoID.unique(), "Serviço", BigDecimal.valueOf(100.0))
        ));

        assertTrue(quantidades.isEmpty());
    }
}
