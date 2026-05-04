package com.autoservice.domain.estoque;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EstoqueIDTest {

    @Test
    void jpaConstructor() throws Exception {
        final Constructor<EstoqueID> c = EstoqueID.class.getDeclaredConstructor();
        c.setAccessible(true);
        assertNull(c.newInstance().getValue());
    }

    @Test
    void uniqueFromStringFromUuid() {
        assertNotNull(EstoqueID.unique().getValue());
        assertEquals("e1", EstoqueID.from("e1").getValue());
        final UUID u = UUID.randomUUID();
        assertEquals(u.toString().toLowerCase(), EstoqueID.from(u).getValue());
    }
}
