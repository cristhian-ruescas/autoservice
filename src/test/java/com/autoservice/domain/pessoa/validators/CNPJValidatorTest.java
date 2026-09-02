package com.autoservice.domain.pessoa.validators;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.valueobject.CNPJ;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CNPJValidator")
class CNPJValidatorTest {

    @Test
    @DisplayName("Deve validar CNPJ bruto válido")
    void deveValidarCnpjBrutoValido() {
        final String cnpj = "12345678000195";

        final boolean result = CNPJValidator.isCnpj(cnpj);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve validar CNPJ formatado válido")
    void deveValidarCnpjFormatadoValido() {
        final String cnpj = "12.345.678/0001-95";

        final boolean result = CNPJValidator.isCnpj(cnpj);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve retornar falso para CNPJ nulo")
    void deveRetornarFalsoParaCnpjNulo() {
        final boolean result = CNPJValidator.isCnpj(null);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar falso para CNPJ com formato inválido (isCnpj)")
    void deveRetornarFalsoParaCnpjFormatoInvalidoNoIsCnpj() {
        final String cnpj = "123";

        final boolean result = CNPJValidator.isCnpj(cnpj);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve validar CNPJ matematicamente válido")
    void deveValidarCnpjMatematicamenteValido() {
        final String cnpj = "11222333000181";

        final boolean result = CNPJValidator.isValidCnpj(cnpj);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ com dígitos verificadores inválidos")
    void deveRejeitarCnpjComDigitosInvalidos() {
        final String cnpj = "11222333000180";

        final boolean result = CNPJValidator.isValidCnpj(cnpj);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar falso para CNPJ com formato inválido (isValidCnpj)")
    void deveRetornarFalsoParaCnpjFormatoInvalidoNoIsValidCnpj() {
        final String cnpj = "123";

        final boolean result = CNPJValidator.isValidCnpj(cnpj);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve lançar exceção para CNPJ inválido")
    void deveLancarExcecaoParaCnpjInvalido() {
        final CNPJ cnpj = CNPJ.from("11111111111111");

        final CNPJValidator validator = new CNPJValidator(
                new ThrowsValidationHandler(),
                cnpj
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "CNPJ inválido",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve validar CNPJ válido com sucesso")
    void deveValidarCnpjValidoComSucesso() {
        final CNPJ cnpj = CNPJ.from("11222333000181");

        final CNPJValidator validator = new CNPJValidator(
                new ThrowsValidationHandler(),
                cnpj
        );

        assertDoesNotThrow(validator::validate);
    }

    @Test
    @DisplayName("Deve lançar exceção para CNPJ com formato inválido")
    void deveLancarExcecaoParaCnpjFormatoInvalido() {
        final CNPJ cnpj = CNPJ.from("123");

        final CNPJValidator validator = new CNPJValidator(
                new ThrowsValidationHandler(),
                cnpj
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "CNPJ deve estar no formato válido (XX.XXX.XXX/XXXX-XX ou XXXXXXXXXXXXXX)",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para CNPJ nulo via construtor protegido")
    void deveLancarExcecaoParaCnpjNulo() throws Exception {
        final var constructor = CNPJ.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final CNPJ cnpj = constructor.newInstance();

        final CNPJValidator validator = new CNPJValidator(
                new ThrowsValidationHandler(),
                cnpj
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
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

        final CNPJValidator validator = new CNPJValidator(
                new ThrowsValidationHandler(),
                cnpj
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "CNPJ não deve ser nulo ou vazio",
                exception.getErrors().getFirst().message()
        );
    }
}
