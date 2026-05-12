package com.autoservice.domain.veiculo.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Marca")
class MarcaTest {

    @Test
    @DisplayName("Deve testar construtor protegido para JPA")
    void testProtectedConstructor() throws Exception {
        var constructor = Marca.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        var marca = constructor.newInstance();

        assertNull(marca.getValue());
    }

    @Test
    @DisplayName("Deve retornar falso se comparado com nulo")
    void testEqualsNull() {
        var marca = Marca.from("Toyota");
        assertFalse(marca.equals(null));
    }

    @Test
    @DisplayName("Deve retornar falso se comparado com classe diferente")
    void testEqualsDifferentClass() {
        var marca = Marca.from("Toyota");
        assertFalse(marca.equals("Toyota"));
    }

    @Test
    @DisplayName("Deve retornar verdadeiro se comparado com a mesma instância")
    void testEqualsSameInstance() {
        var marca = Marca.from("Toyota");
        assertTrue(marca.equals(marca));
    }

    @Test
    @DisplayName("Deve retornar verdadeiro para instâncias diferentes com mesmo valor")
    void testEqualsSameValue() {
        var marca1 = Marca.from("Toyota");
        var marca2 = Marca.from("Toyota");
        assertEquals(marca1, marca2);
    }

    @Test
    @DisplayName("Deve ter mesmo hashCode para marcas iguais")
    void testHashCode() {
        var marca1 = Marca.from("Toyota");
        var marca2 = Marca.from("Toyota");

        assertEquals(marca1.hashCode(), marca2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar valor correto em toString()")
    void testToString() {
        var marca = Marca.from("Toyota");

        assertEquals("Toyota", marca.toString());
    }
}
