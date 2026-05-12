package com.autoservice.domain.veiculo.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kilometragem")
class KilometragemTest {

    @Test
    @DisplayName("Deve testar construtor protegido para JPA")
    void testProtectedConstructor() throws Exception {
        var constructor = Kilometragem.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        var kilometragem = constructor.newInstance();

        assertNull(kilometragem.getValue());
    }

    @Test
    @DisplayName("Deve retornar falso se comparado com nulo")
    void testEqualsNull() {
        var kilometragem = Kilometragem.from(1000);
        assertFalse(kilometragem.equals(null));
    }

    @Test
    @DisplayName("Deve retornar falso se comparado com classe diferente")
    void testEqualsDifferentClass() {
        var kilometragem = Kilometragem.from(1000);
        assertFalse(kilometragem.equals("1000"));
    }

    @Test
    @DisplayName("Deve retornar verdadeiro se comparado com a mesma instância")
    void testEqualsSameInstance() {
        var kilometragem = Kilometragem.from(1000);
        assertTrue(kilometragem.equals(kilometragem));
    }

    @Test
    @DisplayName("Deve retornar verdadeiro para instâncias diferentes com mesmo valor")
    void testEqualsSameValue() {
        var kilometragem1 = Kilometragem.from(1000);
        var kilometragem2 = Kilometragem.from(1000);
        assertEquals(kilometragem1, kilometragem2);
    }

    @Test
    @DisplayName("Deve ter mesmo hashCode para kilometragens iguais")
    void testHashCode() {
        var kilometragem1 = Kilometragem.from(1000);
        var kilometragem2 = Kilometragem.from(1000);

        assertEquals(kilometragem1.hashCode(), kilometragem2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar valor correto em toString()")
    void testToString() {
        var kilometragem = Kilometragem.from(1000);

        assertEquals("1000", kilometragem.toString());
    }
}
