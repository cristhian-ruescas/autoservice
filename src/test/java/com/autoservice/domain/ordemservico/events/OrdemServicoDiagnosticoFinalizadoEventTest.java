package com.autoservice.domain.ordemservico.events;

import com.autoservice.domain.ordemservico.OrdemServicoID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrdemServicoDiagnosticoFinalizadoEventTest {

    @Test
    void evento() {
        final OrdemServicoID id = OrdemServicoID.unique();
        final OrdemServicoDiagnosticoFinalizadoEvent ev = new OrdemServicoDiagnosticoFinalizadoEvent(id);
        assertEquals(id, ev.getOrdemServicoId());
        assertNotNull(ev.occurredOn());
    }
}
