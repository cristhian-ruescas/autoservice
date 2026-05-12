package com.autoservice.domain.veiculo.validators;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.veiculo.valueobject.Modelo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ModeloValidator")
class ModeloValidatorTest {

    @Test
    @DisplayName("Deve validar modelo válido com sucesso")
    void testModeloValido() {
        assertDoesNotThrow(() -> Modelo.from("Civic"));
        var modelo = Modelo.from("Civic");
        assertEquals("Civic", modelo.getValue());
    }

    @Test
    @DisplayName("Deve validar modelo com espaços com sucesso")
    void testModeloValidoComEspacos() {
        assertDoesNotThrow(() -> Modelo.from("Gol Trend"));
        var modelo = Modelo.from("Gol Trend");
        assertEquals("Gol Trend", modelo.getValue());
    }

    @Test
    @DisplayName("Deve validar modelo com números e hífen com sucesso")
    void testModeloValidoComNumerosEHifen() {
        assertDoesNotThrow(() -> Modelo.from("F-150"));
        var modelo = Modelo.from("F-150");
        assertEquals("F-150", modelo.getValue());
    }

    @Test
    @DisplayName("Deve falhar ao validar modelo nulo")
    void testModeloNulo() {
        var erro = assertThrows(DomainException.class, () -> Modelo.from(null));
        assertEquals("Modelo não deve ser nulo", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar modelo vazio")
    void testModeloVazio() {
        var erro = assertThrows(DomainException.class, () -> Modelo.from(""));
        assertEquals("Modelo não deve estar vazio", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar modelo com apenas espaços em branco")
    void testModeloApenasEspacos() {
        var erro = assertThrows(DomainException.class, () -> Modelo.from("   "));
        assertEquals("Modelo não deve estar vazio", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar modelo muito curto (menos de 2 caracteres)")
    void testModeloMuitoCurto() {
        var erro = assertThrows(DomainException.class, () -> Modelo.from("A"));
        assertEquals("Modelo deve ter no mínimo 2 caracteres", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar modelo muito longo (mais de 50 caracteres)")
    void testModeloMuitoLongo() {
        String modeloMuitoLongo = "A".repeat(51);
        var erro = assertThrows(DomainException.class, () -> Modelo.from(modeloMuitoLongo));
        assertEquals("Modelo deve ter no máximo 50 caracteres", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar modelo com caracteres inválidos (símbolos)")
    void testModeloComSimbolos() {
        var erro = assertThrows(DomainException.class, () -> Modelo.from("Civic@"));
        assertEquals("Modelo contém caracteres inválidos", erro.getErrors().getFirst().message());
    }
}
