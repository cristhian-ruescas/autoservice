package com.autoservice.domain.peca;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PecaIDTest {

    @Test
    void jpaConstructor() throws Exception {
        final Constructor<PecaID> c = PecaID.class.getDeclaredConstructor();
        c.setAccessible(true);
        assertNull(c.newInstance().getValue());
    }

    @Test
    void uniqueAndFrom() {
        assertNotNull(PecaID.unique().getValue());
        assertEquals("id", PecaID.from("id").getValue());
        final UUID u = UUID.randomUUID();
        assertEquals(u.toString().toLowerCase(), PecaID.from(u).getValue());
    }

    @Test
    void equalsConsistente() {
        final PecaID a = PecaID.from("p");
        assertEquals(a, PecaID.from("p"));
    }
}
