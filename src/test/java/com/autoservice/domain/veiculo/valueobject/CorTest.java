package com.autoservice.domain.veiculo.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cor")
class CorTest {

    @Test
    @DisplayName("Deve testar construtor protegido para JPA")
    void testProtectedConstructor() throws Exception {
        var constructor = Cor.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        var cor = constructor.newInstance();

        assertNull(cor.getValue());
    }

    @Test
    @DisplayName("Deve retornar falso se comparado com nulo")
    void testEqualsNull() {
        var cor = Cor.from("Preto");
        assertNotEquals(null, cor);
    }

    @Test
    @DisplayName("Deve retornar falso se comparado com classe diferente")
    void testEqualsDifferentClass() {
        var cor = Cor.from("Preto");
        assertNotEquals("Preto", cor);
    }

    @Test
    @DisplayName("Deve retornar verdadeiro para instâncias diferentes com mesmo valor")
    void testEqualsSameValue() {
        var cor1 = Cor.from("Preto");
        var cor2 = Cor.from("Preto");
        assertEquals(cor1, cor2);
    }

    @Test
    @DisplayName("Deve ter mesmo hashCode para cores iguais")
    void testHashCode() {
        var cor1 = Cor.from("Preto");
        var cor2 = Cor.from("Preto");

        assertEquals(cor1.hashCode(), cor2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar valor correto em toString()")
    void testToString() {
        var cor = Cor.from("Preto");

        assertEquals("Preto", cor.toString());
    }
}
