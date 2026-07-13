package com.autoservice.domain.itemservico;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemServicoID")
class ItemServicoIDTest {

    @Test
    void construtorProtegidoJpa() throws Exception {
        final Constructor<ItemServicoID> c = ItemServicoID.class.getDeclaredConstructor();
        c.setAccessible(true);
        assertNull(c.newInstance().getValue());
    }

    @Test
    void unique() {
        assertNotNull(ItemServicoID.unique().getValue());
    }

    @Test
    void fromString() {
        assertEquals("abc", ItemServicoID.from("abc").getValue());
    }

    @Test
    void fromUuid() {
        final UUID u = UUID.randomUUID();
        assertEquals(u.toString().toLowerCase(), ItemServicoID.from(u).getValue());
    }

    @Test
    void equalsHashCode() {
        final ItemServicoID a = ItemServicoID.from("x");
        final ItemServicoID b = ItemServicoID.from("x");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
