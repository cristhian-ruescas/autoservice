package com.autoservice.domain.ordemcompra;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemcompra.enums.OrdemCompraStatus;
import com.autoservice.domain.ordemcompra.events.OrdemCompraRealizadaEvent;
import com.autoservice.domain.ordemcompra.valueobject.DataCompra;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrdemCompra")
class OrdemCompraTest {

    @Test
    void construtorProtegidoJpa() throws Exception {
        final Constructor<OrdemCompra> c = OrdemCompra.class.getDeclaredConstructor();
        c.setAccessible(true);
        assertNull(c.newInstance().getId());
    }

    @Test
    void newOrdemCompra() {
        final OrdemCompra oc = OrdemCompra.newOrdemCompra();
        assertEquals(OrdemCompraStatus.PENDENTE, oc.getStatus());
        assertNotNull(oc.getId());
    }

    @Test
    void realizar() {
        final OrdemCompra oc = OrdemCompra.newOrdemCompra();
        oc.realizar();
        assertEquals(OrdemCompraStatus.REALIZADO, oc.getStatus());
        assertTrue(oc.getDomainEvents().stream().anyMatch(e -> e instanceof OrdemCompraRealizadaEvent));
    }

    @Test
    void realizarForaDePendenteFalha() {
        final OrdemCompra oc = OrdemCompra.with(
                OrdemCompraID.unique(),
                OrdemCompraStatus.REALIZADO,
                DataCompra.from(LocalDate.now()));
        assertThrows(DomainException.class, oc::realizar);
    }

    @Test
    void withReconstroi() {
        final OrdemCompraID id = OrdemCompraID.unique();
        final DataCompra data = DataCompra.from(LocalDate.now().minusDays(1));
        final OrdemCompra oc = OrdemCompra.with(id, OrdemCompraStatus.PENDENTE, data);
        assertEquals(id, oc.getId());
        assertEquals(data, oc.getDataCompra());
    }

    @Test
    void statusNuloFalhaValidacao() {
        assertThrows(DomainException.class, () ->
                OrdemCompra.with(
                        OrdemCompraID.unique(),
                        null,
                        DataCompra.from(LocalDate.now())));
    }
}
