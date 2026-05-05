package com.autoservice.domain.peca;

import com.autoservice.domain.estoque.EstoqueID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Peca")
class PecaTest {

    @Test
    void construtorProtegidoJpa() throws Exception {
        final Constructor<Peca> c = Peca.class.getDeclaredConstructor();
        c.setAccessible(true);
        assertNull(c.newInstance().getId());
    }

    @Test
    void newPecaValida() {
        final EstoqueID estoqueId = EstoqueID.unique();
        final TipoVeiculoID tipoId = TipoVeiculoID.unique();
        final Peca p = Peca.newPeca(
                "Pastilha dianteira", "PST-01", "Bosch", new BigDecimal("99.90"),
                estoqueId, tipoId);

        assertNotNull(p.getId());
        assertEquals("PST-01", p.getCodigo());
        assertEquals(estoqueId, p.getEstoqueId());
        assertEquals(tipoId, p.getTipoVeiculoId());
    }

    @Test
    void vincularEstoque() {
        final Peca p = Peca.newPeca(
                "x", "c1", "m", BigDecimal.ONE, null, TipoVeiculoID.unique());
        final EstoqueID eid = EstoqueID.unique();
        p.vincularEstoque(eid);
        assertEquals(eid, p.getEstoqueId());
    }

    @Test
    void vincularEstoqueNuloFalha() {
        final Peca p = Peca.newPeca(
                "x", "c2", "m", BigDecimal.ONE, EstoqueID.unique(), TipoVeiculoID.unique());
        assertThrows(DomainException.class, () -> p.vincularEstoque(null));
    }

    @Test
    void falhaDescricaoVazia() {
        assertThrows(DomainException.class, () ->
                Peca.newPeca("", "c", "m", BigDecimal.ONE, EstoqueID.unique(), TipoVeiculoID.unique()));
    }

    @Test
    void falhaMarcaLonga() {
        final String marca = "x".repeat(121);
        assertThrows(DomainException.class, () ->
                Peca.newPeca("d", "c3", marca, BigDecimal.ONE, EstoqueID.unique(), TipoVeiculoID.unique()));
    }

    @Test
    void withReconstroi() {
        final PecaID id = PecaID.unique();
        final Peca p = Peca.with(
                id, "d", "c4", "m", new BigDecimal("2"),
                EstoqueID.unique(), TipoVeiculoID.unique());
        assertEquals(id, p.getId());
    }
}
