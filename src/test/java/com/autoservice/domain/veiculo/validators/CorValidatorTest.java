package com.autoservice.domain.veiculo.validators;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.veiculo.valueobject.Cor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CorValidator")
class CorValidatorTest {

    @Test
    @DisplayName("Deve validar cor válida com sucesso")
    void testCorValida() {
        assertDoesNotThrow(() -> Cor.from("Prata"));
        var cor = Cor.from("Prata");
        assertEquals("Prata", cor.getValue());
    }

    @Test
    @DisplayName("Deve validar cor com espaços com sucesso")
    void testCorValidaComEspacos() {
        assertDoesNotThrow(() -> Cor.from("Azul Marinho"));
        var cor = Cor.from("Azul Marinho");
        assertEquals("Azul Marinho", cor.getValue());
    }

    @Test
    @DisplayName("Deve validar cor com acentuação com sucesso")
    void testCorValidaComAcentuacao() {
        assertDoesNotThrow(() -> Cor.from("Vermelho Maçã"));
        var cor = Cor.from("Vermelho Maçã");
        assertEquals("Vermelho Maçã", cor.getValue());
    }

    @Test
    @DisplayName("Deve falhar ao validar cor nula")
    void testCorNula() {
        var erro = assertThrows(DomainException.class, () -> Cor.from(null));
        assertEquals("Cor do veículo não deve ser nula", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar cor vazia")
    void testCorVazia() {
        var erro = assertThrows(DomainException.class, () -> Cor.from(""));
        assertEquals("Cor do veículo não deve estar vazia", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar cor com apenas espaços em branco")
    void testCorApenasEspacos() {
        var erro = assertThrows(DomainException.class, () -> Cor.from("   "));
        assertEquals("Cor do veículo não deve estar vazia", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar cor muito curta (menos de 3 caracteres)")
    void testCorMuitoCurta() {
        var erro = assertThrows(DomainException.class, () -> Cor.from("Az"));
        assertEquals("Cor do veículo deve ter no mínimo 3 caracteres", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar cor muito longa (mais de 30 caracteres)")
    void testCorMuitoLonga() {
        String corMuitoLonga = "A".repeat(31);
        var erro = assertThrows(DomainException.class, () -> Cor.from(corMuitoLonga));
        assertEquals("Cor do veículo deve ter no máximo 30 caracteres", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar cor com caracteres inválidos (números)")
    void testCorComNumeros() {
        var erro = assertThrows(DomainException.class, () -> Cor.from("Azul 123"));
        assertEquals("Cor do veículo deve conter apenas letras e espaços", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar cor com caracteres inválidos (símbolos)")
    void testCorComSimbolos() {
        var erro = assertThrows(DomainException.class, () -> Cor.from("Prata@"));
        assertEquals("Cor do veículo deve conter apenas letras e espaços", erro.getErrors().getFirst().message());
    }
}
