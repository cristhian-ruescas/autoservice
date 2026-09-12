package com.autoservice.domain.veiculo.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Modelo")
class ModeloTest {

    @Test
    @DisplayName("Deve testar construtor protegido para JPA")
    void testProtectedConstructor() throws Exception {
        var constructor = Modelo.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        var modelo = constructor.newInstance();

        assertNull(modelo.getValue());
    }

    @Test
    @DisplayName("Deve retornar falso se comparado com nulo")
    void testEqualsNull() {
        var modelo = Modelo.from("Corolla");
        assertNotEquals(null, modelo);
    }

    @Test
    @DisplayName("Deve retornar falso se comparado com classe diferente")
    void testEqualsDifferentClass() {
        var modelo = Modelo.from("Corolla");
        assertNotEquals("Corolla", modelo);
    }

    @Test
    @DisplayName("Deve retornar verdadeiro para instâncias diferentes com mesmo valor")
    void testEqualsSameValue() {
        var modelo1 = Modelo.from("Corolla");
        var modelo2 = Modelo.from("Corolla");
        assertEquals(modelo1, modelo2);
    }

    @Test
    @DisplayName("Deve ter mesmo hashCode para modelos iguais")
    void testHashCode() {
        var modelo1 = Modelo.from("Corolla");
        var modelo2 = Modelo.from("Corolla");

        assertEquals(modelo1.hashCode(), modelo2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar valor correto em toString()")
    void testToString() {
        var modelo = Modelo.from("Corolla");

        assertEquals("Corolla", modelo.toString());
    }
}
