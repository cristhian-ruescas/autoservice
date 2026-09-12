package com.autoservice.domain.ordemcompra.events;

import com.autoservice.domain.ordemcompra.OrdemCompraID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrdemCompraRealizadaEventTest {

    @Test
    void eventoCarregaId() {
        final OrdemCompraID id = OrdemCompraID.unique();
        final OrdemCompraRealizadaEvent ev = new OrdemCompraRealizadaEvent(id);
        assertEquals(id, ev.getOrdemCompraId());
        assertNotNull(ev.occurredOn());
    }
}
