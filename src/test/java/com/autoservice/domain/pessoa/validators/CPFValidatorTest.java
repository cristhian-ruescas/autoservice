package com.autoservice.domain.pessoa.validators;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CPFValidator")
class CPFValidatorTest {

    @Test
    @DisplayName("Deve retornar true para CPF válido sem formatação")
    void testIsCpfRawValido() {
        final String cpf = "52998224725";

        final boolean result = CPFValidator.isCpf(cpf);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve retornar true para CPF válido com formatação")
    void testIsCpfFormattedValido() {
        final String cpf = "529.982.247-25";

        final boolean result = CPFValidator.isCpf(cpf);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve retornar false quando CPF for nulo")
    void testIsCpfNull() {
        final boolean result = CPFValidator.isCpf(null);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar false para CPF com formato inválido")
    void testIsCpfFormatoInvalido() {
        final String cpf = "123";

        final boolean result = CPFValidator.isCpf(cpf);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar true para CPF matematicamente válido")
    void testIsValidCpfValido() {
        final String cpf = "52998224725";

        final boolean result = CPFValidator.isValidCpf(cpf);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve retornar false para CPF com todos os dígitos iguais")
    void testIsValidCpfTodosDigitosIguais() {
        final String cpf = "11111111111";

        final boolean result = CPFValidator.isValidCpf(cpf);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar false para CPF com dígitos verificadores inválidos")
    void testIsValidCpfDigitosInvalidos() {
        final String cpf = "52998224724";

        final boolean result = CPFValidator.isValidCpf(cpf);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar false para CPF com formato inválido")
    void testIsValidCpfFormatoInvalido() {
        final String cpf = "123";

        final boolean result = CPFValidator.isValidCpf(cpf);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF for inválido")
    void testValidateCpfInvalido() {
        final CPF cpf = CPF.from("11111111111");

        final CPFValidator validator = new CPFValidator(
                cpf,
                new ThrowsValidationHandler()
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "CPF inválido",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve validar CPF corretamente quando for válido")
    void testValidateCpfValido() {
        final CPF cpf = CPF.from("52998224725");

        final CPFValidator validator = new CPFValidator(
                cpf,
                new ThrowsValidationHandler()
        );

        assertDoesNotThrow(validator::validate);
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF estiver com formato inválido")
    void testValidateCpfFormatoInvalido() {
        final CPF cpf = CPF.from("123");

        final CPFValidator validator = new CPFValidator(
                cpf,
                new ThrowsValidationHandler()
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "CPF deve estar no formato válido (XXX.XXX.XXX-XX ou XXXXXXXXXXX)",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF for nulo")
    void testValidateCpfNull() throws Exception {
        final var constructor = CPF.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final CPF cpf = constructor.newInstance();

        final CPFValidator validator = new CPFValidator(
                cpf,
                new ThrowsValidationHandler()
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "CPF não deve ser nulo ou vazio",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF estiver vazio")
    void testValidateCpfBlank() {
        final CPF cpf = CPF.from("");

        final CPFValidator validator = new CPFValidator(
                cpf,
                new ThrowsValidationHandler()
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "CPF não deve ser nulo ou vazio",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve validar CPF que cobre retorno de dígito zero no primeiro dígito")
    void testCpfPrimeiroDigitoZero() {
        final String cpf = "12345678909";

        final boolean result = CPFValidator.isValidCpf(cpf);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve validar CPF que cobre retorno de dígito zero no segundo dígito")
    void testCpfSegundoDigitoZero() {
        final String cpf = "98765432100";

        final boolean result = CPFValidator.isValidCpf(cpf);

        assertTrue(result);
    }
}
