package com.autoservice.domain.ordemcompra;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.peca.PecaID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemOrdemCompra")
class ItemOrdemCompraTest {

    @Test
    void construtorProtegidoJpa() throws Exception {
        final Constructor<ItemOrdemCompra> c = ItemOrdemCompra.class.getDeclaredConstructor();
        c.setAccessible(true);
        assertNull(c.newInstance().getId());
    }

    @Test
    void newItem() {
        final OrdemCompraID ocId = OrdemCompraID.unique();
        final PecaID pecaId = PecaID.unique();
        final ItemOrdemCompra item = ItemOrdemCompra.newItem(ocId, pecaId, 5);

        assertEquals(ocId, item.getOrdemCompraId());
        assertEquals(pecaId, item.getPecaId());
        assertEquals(5, item.getQuantidade());
    }

    @Test
    void falhaSemOrdemCompra() {
        assertThrows(DomainException.class, () ->
                ItemOrdemCompra.newItem(null, PecaID.unique(), 1));
    }

    @Test
    void falhaSemPeca() {
        assertThrows(DomainException.class, () ->
                ItemOrdemCompra.newItem(OrdemCompraID.unique(), null, 1));
    }

    @Test
    void falhaQuantidadeInvalida() {
        assertThrows(DomainException.class, () ->
                ItemOrdemCompra.newItem(OrdemCompraID.unique(), PecaID.unique(), 0));
    }
}
