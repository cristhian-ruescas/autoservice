package com.autoservice.domain.cliente.valueobject;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DataCadastro")
class DataCadastroTest {

    @Test
    @DisplayName("Deve criar DataCadastro com data válida")
    void deveCriarDataCadastroValida() {
        final LocalDate data = LocalDate.of(2026, 5, 1);

        final DataCadastro dataCadastro = DataCadastro.from(data);

        assertNotNull(dataCadastro);
        assertEquals(data, dataCadastro.getValue());
    }

    @Test
    @DisplayName("Deve criar DataCadastro com data atual usando today")
    void deveCriarDataCadastroComDataAtual() {
        final LocalDate today = LocalDate.now();

        final DataCadastro dataCadastro = DataCadastro.from(today);

        assertNotNull(dataCadastro);
        assertEquals(today, dataCadastro.getValue());
    }

    @Test
    @DisplayName("Deve validar DataCadastro com sucesso")
    void deveValidarDataCadastroComSucesso() {
        final LocalDate today = LocalDate.now();
        final DataCadastro dataCadastro = DataCadastro.from(today);

        assertDoesNotThrow(() ->
                dataCadastro.validate(new ThrowsValidationHandler())
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para data de cadastro muito antiga")
    void deveLancarExcecaoParaDataMuitoAntiga() {
        final LocalDate dataAntiga = LocalDate.now().minusYears(11);

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> DataCadastro.from(dataAntiga)
        );

        assertEquals(
                "Data de cadastro não pode ser anterior a 10 anos",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para data de cadastro futura")
    void deveLancarExcecaoParaDataFutura() {
        final LocalDate dataFutura = LocalDate.now().plusDays(1);

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> DataCadastro.from(dataFutura)
        );

        assertEquals(
                "Data de cadastro não pode ser no futuro",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para data de cadastro nula")
    void deveLancarExcecaoParaDataNula() {
        var exception = assertThrows(NullPointerException.class, () -> DataCadastro.from(null));
        assertEquals("Data não pode ser nula", exception.getMessage());
    }

    @Test
    @DisplayName("Deve cobrir construtor protegido do JPA")
    void deveCobrirConstrutorProtegido() throws Exception {
        final var constructor = DataCadastro.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final DataCadastro dataCadastro = constructor.newInstance();

        assertNull(dataCadastro.getValue());
        assertEquals("null", dataCadastro.toString());
    }

    @Test
    @DisplayName("Deve comparar DataCadastro iguais corretamente")
    void deveCompararDataCadastroIguais() {
        final LocalDate data = LocalDate.of(2026, 5, 1);

        final DataCadastro data1 = DataCadastro.from(data);
        final DataCadastro data2 = DataCadastro.from(data);

        assertEquals(data1, data2);
    }

    @Test
    @DisplayName("Deve retornar falso ao comparar com null")
    void deveRetornarFalsoAoCompararComNull() {
        final LocalDate today = LocalDate.now();
        final DataCadastro dataCadastro = DataCadastro.from(today);

        assertNotEquals( null, dataCadastro);
    }


    @Test
    @DisplayName("Deve gerar hashCode consistente para objetos iguais")
    void deveGerarHashCodeConsistente() {
        final LocalDate data = LocalDate.of(2026, 5, 1);

        final DataCadastro data1 = DataCadastro.from(data);
        final DataCadastro data2 = DataCadastro.from(data);

        assertEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar data formatada corretamente no toString")
    void deveRetornarDataFormatadaNoToString() {
        final LocalDate data = LocalDate.of(2025, 4, 30);

        final DataCadastro dataCadastro = DataCadastro.from(data);

        assertEquals("30-04-2025", dataCadastro.toString());
    }
}
