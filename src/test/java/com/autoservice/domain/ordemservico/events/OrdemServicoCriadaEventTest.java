package com.autoservice.domain.ordemservico.events;

import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.veiculo.VeiculoID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("OrdemServicoCriadaEvent")
class OrdemServicoCriadaEventTest {

    @Test
    @DisplayName("Deve criar evento de ordem de servico criada corretamente")
    void deveCriarEventoCorretamente() {
        final OrdemServicoID osID = OrdemServicoID.unique();
        final VeiculoID veiculoId = VeiculoID.unique();

        final OrdemServicoCriadaEvent event = new OrdemServicoCriadaEvent(osID, veiculoId);

        assertNotNull(event);
        assertNotNull(event.occurredOn());
        assertEquals(osID, event.getOrdemServicoId());
        assertEquals(veiculoId, event.getVeiculoId());
    }
}