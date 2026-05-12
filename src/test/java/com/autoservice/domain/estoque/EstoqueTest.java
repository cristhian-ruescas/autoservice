package com.autoservice.domain.estoque;

import com.autoservice.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Estoque")
class EstoqueTest {

    @Test
    void construtorProtegidoJpa() throws Exception {
        final Constructor<Estoque> c = Estoque.class.getDeclaredConstructor();
        c.setAccessible(true);
        assertNull(c.newInstance().getId());
    }

    @Test
    void newEstoqueValido() {
        final Estoque e = Estoque.newEstoque(100, 10, "Prateleira A1");
        assertEquals(100, e.getQuantidadeDisponivel());
        assertEquals(10, e.getQuantidadeMinima());
        assertEquals("Prateleira A1", e.getLocalizacao());
    }

    @Test
    void baixarEAdicionar() {
        final Estoque e = Estoque.newEstoque(50, 5, null);
        e.baixar(20);
        assertEquals(30, e.getQuantidadeDisponivel());
        e.adicionar(5);
        assertEquals(35, e.getQuantidadeDisponivel());
    }

    @Test
    void baixarQuantidadeInvalida() {
        final Estoque e = Estoque.newEstoque(5, 1, null);
        assertThrows(DomainException.class, () -> e.baixar(0));
        assertThrows(DomainException.class, () -> e.baixar(10));
    }

    @Test
    void alterarLocalizacao() {
        final Estoque e = Estoque.newEstoque(1, 1, "A");
        e.alterarLocalizacao("B");
        assertEquals("B", e.getLocalizacao());
    }

    @Test
    void localizacaoLongaFalha() {
        final Estoque e = Estoque.newEstoque(1, 1, "ok");
        assertThrows(DomainException.class, () ->
                e.alterarLocalizacao("x".repeat(121)));
    }

    @Test
    void quantidadeDisponivelNegativaFalha() {
        assertThrows(DomainException.class, () ->
                Estoque.newEstoque(-1, 0, null));
    }
}
