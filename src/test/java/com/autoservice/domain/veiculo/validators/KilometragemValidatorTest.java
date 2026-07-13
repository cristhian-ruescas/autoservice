package com.autoservice.domain.veiculo.validators;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.veiculo.valueobject.Kilometragem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("KilometragemValidator")
class KilometragemValidatorTest {

    @Test
    @DisplayName("Deve validar kilometragem zero com sucesso")
    void testKilometragemZero() {
        assertDoesNotThrow(() -> Kilometragem.from(0));
        var kilometragem = Kilometragem.from(0);
        assertEquals(0, kilometragem.getValue());
    }

    @Test
    @DisplayName("Deve validar kilometragem positiva com sucesso")
    void testKilometragemPositiva() {
        assertDoesNotThrow(() -> Kilometragem.from(15000));
        var kilometragem = Kilometragem.from(15000);
        assertEquals(15000, kilometragem.getValue());
    }

    @Test
    @DisplayName("Deve falhar ao validar kilometragem nula")
    void testKilometragemNula() {
        var erro = assertThrows(DomainException.class, () -> Kilometragem.from(null));
        assertEquals("Kilometragem não deve ser nula", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar kilometragem negativa")
    void testKilometragemNegativa() {
        var erro = assertThrows(DomainException.class, () -> Kilometragem.from(-10));
        assertEquals("Kilometragem não pode ser negativa", erro.getErrors().getFirst().message());
    }
}
