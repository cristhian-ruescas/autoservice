package com.autoservice.domain.cliente.validators;

import com.autoservice.domain.cliente.valueobject.DataCadastro;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DataCadastroValidator")
class DataCadastroValidatorTest {

    @Test
    @DisplayName("Deve validar data de cadastro válida")
    void deveValidarDataCadastroValida() {
        final var dataCadastro = DataCadastro.from(LocalDate.now());

        final var validator = new DataCadastroValidator(
                dataCadastro,
                new ThrowsValidationHandler()
        );

        assertDoesNotThrow(validator::validate);
    }

    @Test
    @DisplayName("Deve rejeitar data de cadastro nula")
    void deveRejeitarDataCadastroNula() throws Exception {
        final var constructor = DataCadastro.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var dataCadastro = constructor.newInstance();

        final var validator = new DataCadastroValidator(
                dataCadastro,
                new ThrowsValidationHandler()
        );

        assertThrows(DomainException.class, validator::validate);
    }

    @Test
    @DisplayName("Deve rejeitar data de cadastro anterior ao limite permitido")
    void deveRejeitarDataCadastroMuitoAntiga() {
        final DomainException exception = assertThrows(
                DomainException.class,
                () -> DataCadastro.from(LocalDate.now().minusYears(11))
        );

        assertEquals(
                "Data de cadastro não pode ser anterior a 10 anos",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve rejeitar data de cadastro no futuro")
    void deveRejeitarDataCadastroNoFuturo() {
        final DomainException exception = assertThrows(
                DomainException.class,
                () -> DataCadastro.from(LocalDate.now().plusDays(1))
        );

        assertEquals(
                "Data de cadastro não pode ser no futuro",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve aceitar data de cadastro exatamente no limite passado")
    void deveAceitarDataCadastroNoLimitePassado() {
        final var dataCadastro = DataCadastro.from(LocalDate.now().minusYears(10));

        final var validator = new DataCadastroValidator(
                dataCadastro,
                new ThrowsValidationHandler()
        );

        assertDoesNotThrow(validator::validate);
    }

    @Test
    @DisplayName("Deve aceitar data de cadastro atual como limite futuro")
    void deveAceitarDataCadastroNoLimiteFuturo() {
        final var dataCadastro = DataCadastro.from(LocalDate.now());

        final var validator = new DataCadastroValidator(
                dataCadastro,
                new ThrowsValidationHandler()
        );

        assertDoesNotThrow(validator::validate);
    }
}