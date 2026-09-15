package com.autoservice.domain.ordemcompra;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

class ItemOrdemCompraIDTest {

    @Test
    void jpaConstructor() throws Exception {
        final Constructor<ItemOrdemCompraID> c = ItemOrdemCompraID.class.getDeclaredConstructor();
        c.setAccessible(true);
        assertNull(c.newInstance().getValue());
    }

    @Test
    void uniqueAndFrom() {
        assertNotNull(ItemOrdemCompraID.unique().getValue());
        assertEquals("ioc", ItemOrdemCompraID.from("ioc").getValue());
    }

    @Test
    void equalsHashCode() {
        final ItemOrdemCompraID a = ItemOrdemCompraID.from("z");
        assertEquals(a, ItemOrdemCompraID.from("z"));
        assertEquals(a.hashCode(), ItemOrdemCompraID.from("z").hashCode());
    }
}
