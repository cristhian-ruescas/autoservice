package com.autoservice.domain.ordemservico.valueobject;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DataCriacao")
class DataCriacaoTest {

    @Test
    @DisplayName("Deve testar construtor protegido para JPA")
    void testProtectedConstructor() throws Exception {
        final Constructor<DataCriacao> constructor = DataCriacao.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final DataCriacao dataCriacao = constructor.newInstance();

        assertNull(dataCriacao.getValue());
        assertEquals("null", dataCriacao.toString());
    }

    @Test
    @DisplayName("Deve falhar ao criar DataCriacao com valor nulo")
    void deveFalharAoCriarComValorNulo() {
        var exception = assertThrows(NullPointerException.class, () -> DataCriacao.from(null));
        assertEquals("Data de criação não pode ser nula", exception.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao validar DataCriacao com data futura")
    void deveFalharAoValidarDataFutura() {
        final LocalDate dataFutura = LocalDate.now().plusDays(5);
        var exception = assertThrows(DomainException.class, () -> DataCriacao.from(dataFutura));

        assertEquals("Data de criação não pode ser no futuro", exception.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve criar DataCriacao valida com data atual")
    void deveCriarDataValidaAtual() {
        final LocalDate hoje = LocalDate.now();
        final DataCriacao data = DataCriacao.from(hoje);

        assertNotNull(data);
        assertEquals(hoje, data.getValue());
        assertDoesNotThrow(() -> data.validate(new ThrowsValidationHandler()));
    }

    @Test
    @DisplayName("Deve criar DataCriacao valida com data passada")
    void deveCriarDataValidaPassada() {
        final LocalDate ontem = LocalDate.now().minusDays(1);
        final DataCriacao data = DataCriacao.from(ontem);

        assertNotNull(data);
        assertEquals(ontem, data.getValue());
    }

    @Test
    @DisplayName("Deve validar logica de equals e hashCode")
    void deveValidarEqualsEHashCode() {
        final LocalDate hoje = LocalDate.now();
        final LocalDate ontem = LocalDate.now().minusDays(1);

        final DataCriacao d1 = DataCriacao.from(hoje);
        final DataCriacao d2 = DataCriacao.from(hoje);
        final DataCriacao d3 = DataCriacao.from(ontem);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());

        assertNotEquals(d1, d3);
        assertNotEquals(null, d1);
        assertNotEquals("hoje", d1);
    }

    @Test
    @DisplayName("Deve formatar data corretamente no toString")
    void deveFormatarToString() {
        final LocalDate data = LocalDate.of(2025, 4, 30);
        final DataCriacao dataCriacao = DataCriacao.from(data);

        assertEquals("30-04-2025", dataCriacao.toString());
    }
}
