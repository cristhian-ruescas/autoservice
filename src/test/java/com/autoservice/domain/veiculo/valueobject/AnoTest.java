package com.autoservice.domain.veiculo.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Ano")
class AnoTest {

    @Test
    @DisplayName("Deve testar construtor protegido para JPA")
    void testProtectedConstructor() throws Exception {
        var constructor = Ano.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        var ano = constructor.newInstance();

        assertNull(ano.getValue());
    }

    @Test
    @DisplayName("Deve retornar falso se comparado com nulo")
    void testEqualsNull() {
        var ano = Ano.from(2023);
        assertNotEquals(null, ano);
    }

    @Test
    @DisplayName("Deve retornar falso se comparado com classe diferente")
    void testEqualsDifferentClass() {
        var ano = Ano.from(2023);
        assertNotEquals("2023", ano);
    }

    @Test
    @DisplayName("Deve retornar verdadeiro se comparado com a mesma instância")
    void testEqualsSameInstance() {
        var ano = Ano.from(2023);
        assertEquals(ano, ano);
    }

    @Test
    @DisplayName("Deve retornar verdadeiro para instâncias diferentes com mesmo valor")
    void testEqualsSameValue() {
        var ano1 = Ano.from(2023);
        var ano2 = Ano.from(2023);
        assertEquals(ano1, ano2);
    }

    @Test
    @DisplayName("Deve ter mesmo hashCode para anos iguais")
    void testHashCode() {
        var ano1 = Ano.from(2023);
        var ano2 = Ano.from(2023);

        assertEquals(ano1.hashCode(), ano2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar valor correto em toString()")
    void testToString() {
        var ano = Ano.from(2023);

        assertEquals("2023", ano.toString());
    }
}
