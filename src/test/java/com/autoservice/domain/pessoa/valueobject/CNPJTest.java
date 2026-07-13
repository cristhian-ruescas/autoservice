package com.autoservice.domain.pessoa.valueobject;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CNPJ")
class CNPJTest {

    @Test
    @DisplayName("Deve criar CNPJ válido normalmente")
    void deveCriarCnpjValido() {
        assertDoesNotThrow(() -> CNPJ.from("11222333000181"));
    }

    @Test
    @DisplayName("Deve lançar exceção para CNPJ inválido")
    void deveLancarExcecaoParaCnpjInvalido() {
        final CNPJ cnpj = CNPJ.from("11222333000180");

        final ThrowsValidationHandler handler = new ThrowsValidationHandler();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> cnpj.validate(handler)
        );

        assertEquals(
                "CNPJ inválido",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para formato de CNPJ inválido")
    void deveLancarExcecaoParaFormatoInvalido() {
        final CNPJ cnpj = CNPJ.from("123");

        final ThrowsValidationHandler handler = new ThrowsValidationHandler();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> cnpj.validate(handler)
        );

        assertEquals(
                "CNPJ deve estar no formato válido (XX.XXX.XXX/XXXX-XX ou XXXXXXXXXXXXXX)",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para CNPJ nulo")
    void deveLancarExcecaoParaCnpjNulo() {
        final CNPJ cnpj = CNPJ.from(null);

        final ThrowsValidationHandler handler = new ThrowsValidationHandler();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> cnpj.validate(handler)
        );

        assertEquals(
                "CNPJ não deve ser nulo ou vazio",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para CNPJ vazio")
    void deveLancarExcecaoParaCnpjVazio() {
        final CNPJ cnpj = CNPJ.from("");

        final ThrowsValidationHandler handler = new ThrowsValidationHandler();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> cnpj.validate(handler)
        );

        assertEquals(
                "CNPJ não deve ser nulo ou vazio",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve cobrir construtor protegido do JPA")
    void deveCobrirConstrutorProtegido() throws Exception {
        final var constructor = CNPJ.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final CNPJ cnpj = constructor.newInstance();

        assertNull(cnpj.getValue());
    }

    @Test
    @DisplayName("Deve validar equals corretamente")
    void deveValidarEqualsCorretamente() {
        final CNPJ cnpj1 = CNPJ.from("11222333000181");
        final CNPJ cnpj2 = CNPJ.from("11222333000181");
        final CNPJ cnpj3 = CNPJ.from("11222333000180");

        assertEquals(cnpj1, cnpj2);
        assertNotEquals(cnpj1, cnpj3);
        assertNotEquals( null, cnpj1);
    }

    @Test
    @DisplayName("Deve gerar hashCode consistente")
    void deveGerarHashCodeConsistente() {
        final CNPJ cnpj1 = CNPJ.from("11222333000181");
        final CNPJ cnpj2 = CNPJ.from("11222333000181");

        assertEquals(cnpj1.hashCode(), cnpj2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar valor no toString")
    void deveRetornarValorNoToString() {
        final CNPJ cnpj = CNPJ.from("11222333000181");

        assertEquals("11222333000181", cnpj.toString());
    }

    @Test
    @DisplayName("Deve manter comportamento com construtor protegido nulo")
    void deveManterComportamentoComConstrutorProtegido() throws Exception {
        final var constructor = CNPJ.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final CNPJ cnpj = constructor.newInstance();

        assertNull(cnpj.getValue());
        assertNull(cnpj.toString());
    }

    @Test
    @DisplayName("Deve retornar falso ao comparar com null")
    void deveRetornarFalsoAoCompararComNull() {
        final CNPJ cnpj = CNPJ.from("11222333000181");

        assertNotEquals(null, cnpj);
    }

    @Test
    @DisplayName("Deve retornar falso ao comparar com tipo diferente")
    void deveRetornarFalsoAoCompararComTipoDiferente() {
        final CNPJ cnpj = CNPJ.from("11222333000181");

        assertNotEquals("11222333000181", cnpj);
    }

    @Test
    @DisplayName("Deve retornar verdadeiro para CNPJs iguais")
    void deveRetornarVerdadeiroParaCnpjsIguais() {
        final CNPJ cnpj1 = CNPJ.from("11222333000181");
        final CNPJ cnpj2 = CNPJ.from("11222333000181");

        assertEquals(cnpj1, cnpj2);
    }
}
