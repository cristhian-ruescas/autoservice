package com.autoservice.domain.veiculo.validators;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.veiculo.valueobject.Marca;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MarcaValidator")
class MarcaValidatorTest {

    @Test
    @DisplayName("Deve validar marca válida com sucesso")
    void testMarcaValida() {
        assertDoesNotThrow(() -> Marca.from("Toyota"));
        var marca = Marca.from("Toyota");
        assertEquals("Toyota", marca.getValue());
    }

    @Test
    @DisplayName("Deve validar marca com espaços com sucesso")
    void testMarcaValidaComEspacos() {
        assertDoesNotThrow(() -> Marca.from("Aston Martin"));
        var marca = Marca.from("Aston Martin");
        assertEquals("Aston Martin", marca.getValue());
    }

    @Test
    @DisplayName("Deve falhar ao validar marca nula")
    void testMarcaNula() {
        var erro = assertThrows(DomainException.class, () -> Marca.from(null));
        assertEquals("Marca não deve ser nula", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar marca vazia")
    void testMarcaVazia() {
        var erro = assertThrows(DomainException.class, () -> Marca.from(""));
        assertEquals("Marca não deve estar vazia", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar marca com apenas espaços em branco")
    void testMarcaApenasEspacos() {
        var erro = assertThrows(DomainException.class, () -> Marca.from("   "));
        assertEquals("Marca não deve estar vazia", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar marca muito curta (menos de 2 caracteres)")
    void testMarcaMuitoCurta() {
        var erro = assertThrows(DomainException.class, () -> Marca.from("A"));
        assertEquals("Marca deve ter no mínimo 2 caracteres", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar marca muito longa (mais de 50 caracteres)")
    void testMarcaMuitoLonga() {
        String marcaMuitoLonga = "A".repeat(51);
        var erro = assertThrows(DomainException.class, () -> Marca.from(marcaMuitoLonga));
        assertEquals("Marca deve ter no máximo 50 caracteres", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar marca com caracteres inválidos (números)")
    void testMarcaComNumeros() {
        var erro = assertThrows(DomainException.class, () -> Marca.from("Ford 123"));
        assertEquals("Marca deve conter apenas letras e espaços", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar marca com caracteres inválidos (símbolos)")
    void testMarcaComSimbolos() {
        var erro = assertThrows(DomainException.class, () -> Marca.from("VW@"));
        assertEquals("Marca deve conter apenas letras e espaços", erro.getErrors().getFirst().message());
    }
}
