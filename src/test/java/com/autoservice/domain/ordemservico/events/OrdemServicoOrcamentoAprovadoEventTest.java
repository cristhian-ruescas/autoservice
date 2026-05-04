package com.autoservice.domain.ordemservico.events;

import com.autoservice.domain.ordemservico.OrdemServicoID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrdemServicoOrcamentoAprovadoEventTest {

    @Test
    void evento() {
        final OrdemServicoID id = OrdemServicoID.unique();
        final OrdemServicoOrcamentoAprovadoEvent ev = new OrdemServicoOrcamentoAprovadoEvent(id);
        assertEquals(id, ev.getOrdemServicoId());
        assertNotNull(ev.occurredOn());
    }
}
