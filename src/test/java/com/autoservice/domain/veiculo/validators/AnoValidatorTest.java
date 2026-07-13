package com.autoservice.domain.veiculo.validators;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.veiculo.valueobject.Ano;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AnoValidator")
class AnoValidatorTest {

    @Test
    @DisplayName("Deve falhar ao validar ano nulo")
    void testAnoNulo() {
        var erro = assertThrows(DomainException.class, () -> Ano.from(null));
        assertEquals("Ano não deve ser nulo", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve validar ano válido com sucesso")
    void testAnoValido() {
        assertDoesNotThrow(() -> Ano.from(2020));
        var ano = Ano.from(2020);
        assertEquals(2020, ano.getValue());
    }

    @Test
    @DisplayName("Deve validar ano no limite mínimo (1886) com sucesso")
    void testAnoLimiteMinimo() {
        assertDoesNotThrow(() -> Ano.from(1886));
        var ano = Ano.from(1886);
        assertEquals(1886, ano.getValue());
    }

    @Test
    @DisplayName("Deve validar ano no limite máximo (ano atual + 1) com sucesso")
    void testAnoLimiteMaximo() {
        int anoMaximo = LocalDate.now().getYear() + 1;
        assertDoesNotThrow(() -> Ano.from(anoMaximo));
        var ano = Ano.from(anoMaximo);
        assertEquals(anoMaximo, ano.getValue());
    }

    @Test
    @DisplayName("Deve falhar ao validar ano abaixo do limite mínimo (1885)")
    void testAnoAbaixoLimiteMinimo() {
        var erro = assertThrows(DomainException.class, () -> Ano.from(1885));
        assertEquals("Ano não pode ser menor que 1886", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar ano acima do limite máximo (ano atual + 2)")
    void testAnoAcimaLimiteMaximo() {
        int anoInvalido = LocalDate.now().getYear() + 2;
        var erro = assertThrows(DomainException.class, () -> Ano.from(anoInvalido));
        assertEquals(String.format("Ano não pode ser maior que %d", LocalDate.now().getYear() + 1), erro.getErrors().getFirst().message());
    }
}
