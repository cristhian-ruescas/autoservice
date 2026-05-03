package com.autoservice.domain.veiculo.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Placa")
class PlacaTest {

    @Test
    @DisplayName("Deve testar construtor protegido para JPA")
    void testProtectedConstructor() throws Exception {
        var constructor = Placa.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        var placa = constructor.newInstance();

        assertNull(placa.getValue());
    }

    @Test
    @DisplayName("Deve retornar falso se comparado com nulo")
    void testEqualsNull() {
        var placa = Placa.from("ABC1D23");
        assertFalse(placa.equals(null));
    }

    @Test
    @DisplayName("Deve retornar falso se comparado com classe diferente")
    void testEqualsDifferentClass() {
        var placa = Placa.from("ABC1D23");
        assertFalse(placa.equals("ABC1D23"));
    }

    @Test
    @DisplayName("Deve retornar verdadeiro se comparado com a mesma instância")
    void testEqualsSameInstance() {
        var placa = Placa.from("ABC1D23");
        assertTrue(placa.equals(placa));
    }

    @Test
    @DisplayName("Deve retornar verdadeiro para instâncias diferentes com mesmo valor")
    void testEqualsSameValue() {
        var placa1 = Placa.from("ABC1D23");
        var placa2 = Placa.from("ABC1D23");
        assertEquals(placa1, placa2);
    }

    @Test
    @DisplayName("Deve ter mesmo hashCode para placas iguais")
    void testHashCode() {
        var placa1 = Placa.from("ABC1D23");
        var placa2 = Placa.from("ABC1D23");

        assertEquals(placa1.hashCode(), placa2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar valor correto em toString()")
    void testToString() {
        var placa = Placa.from("ABC1D23");

        assertEquals("ABC1D23", placa.toString());
    }
}
