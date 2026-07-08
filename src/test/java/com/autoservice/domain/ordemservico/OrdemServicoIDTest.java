package com.autoservice.domain.ordemservico;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrdemServicoID")
class OrdemServicoIDTest {

    @Test
    @DisplayName("Deve testar construtor protegido para JPA")
    void testProtectedConstructor() throws Exception {
        final Constructor<OrdemServicoID> constructor = OrdemServicoID.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final OrdemServicoID id = constructor.newInstance();

        assertNull(id.getValue());
    }

    @Test
    @DisplayName("Deve gerar UUID valido a partir da factory unique()")
    void deveGerarUUID() {
        final OrdemServicoID id = OrdemServicoID.unique();

        assertNotNull(id);
        assertNotNull(id.getValue());
    }

    @Test
    @DisplayName("Deve inicializar id a partir de string factory from()")
    void deveInicializarDeString() {
        final OrdemServicoID id = OrdemServicoID.from("123");

        assertNotNull(id);
        assertEquals("123", id.getValue());
    }

    @Test
    @DisplayName("Deve lançar exceção se inicializar com nulo")
    void deveLancarExceptionSeInicializarComNulo() {
        assertThrows(NullPointerException.class, () -> OrdemServicoID.from((String) null));
    }

    @Test
    @DisplayName("Deve inicializar id a partir de UUID factory from()")
    void deveInicializarDeUUID() {
        final UUID uuid = UUID.randomUUID();
        final OrdemServicoID id = OrdemServicoID.from(uuid);

        assertNotNull(id);
        assertEquals(uuid.toString().toLowerCase(), id.getValue());
    }

    @Test
    @DisplayName("Deve validar logica de equals e hascode")
    void deveValidarEqualsHashCode() {
        final OrdemServicoID id1 = OrdemServicoID.from("123");
        final OrdemServicoID id2 = OrdemServicoID.from("123");
        final OrdemServicoID id3 = OrdemServicoID.from("abc");

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());

        assertNotEquals(id1, id3);
        assertNotEquals(null, id1);
        assertNotEquals("123", id1);
    }
}
