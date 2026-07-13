package com.autoservice.domain.ordemcompra;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrdemCompraIDTest {

    @Test
    void jpaConstructor() throws Exception {
        final Constructor<OrdemCompraID> c = OrdemCompraID.class.getDeclaredConstructor();
        c.setAccessible(true);
        assertNull(c.newInstance().getValue());
    }

    @Test
    void uniqueFromStringFromUuid() {
        assertNotNull(OrdemCompraID.unique().getValue());
        assertEquals("oc", OrdemCompraID.from("oc").getValue());
        final UUID u = UUID.randomUUID();
        assertEquals(u.toString().toLowerCase(), OrdemCompraID.from(u).getValue());
    }
}
