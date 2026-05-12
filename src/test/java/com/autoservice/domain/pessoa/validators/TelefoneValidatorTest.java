package com.autoservice.domain.pessoa.validators;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TelefoneValidator")
class TelefoneValidatorTest {

    @Test
    @DisplayName("Deve validar telefone válido com 10 dígitos")
    void deveValidarTelefoneCom10Digitos() {
        final Telefone telefone = Telefone.from("1133334444");

        final TelefoneValidator validator = new TelefoneValidator(
                telefone,
                new ThrowsValidationHandler()
        );

        assertDoesNotThrow(validator::validate);
    }

    @Test
    @DisplayName("Deve validar telefone válido com 11 dígitos")
    void deveValidarTelefoneCom11Digitos() {
        final Telefone telefone = Telefone.from("11999998888");

        final TelefoneValidator validator = new TelefoneValidator(
                telefone,
                new ThrowsValidationHandler()
        );

        assertDoesNotThrow(validator::validate);
    }

    @Test
    @DisplayName("Deve lançar exceção para telefone nulo")
    void deveLancarExcecaoParaTelefoneNulo() throws Exception {
        final var constructor = Telefone.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final Telefone telefone = constructor.newInstance();

        final TelefoneValidator validator = new TelefoneValidator(
                telefone,
                new ThrowsValidationHandler()
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "Telefone não deve ser nulo ou vazio",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para telefone vazio")
    void deveLancarExcecaoParaTelefoneVazio() {
        final Telefone telefone = Telefone.from("");

        final TelefoneValidator validator = new TelefoneValidator(
                telefone,
                new ThrowsValidationHandler()
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "Telefone não deve ser nulo ou vazio",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para telefone com menos de 10 dígitos")
    void deveLancarExcecaoParaTelefoneMenorQue10Digitos() {
        final Telefone telefone = Telefone.from("123456789");

        final TelefoneValidator validator = new TelefoneValidator(
                telefone,
                new ThrowsValidationHandler()
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "Telefone deve conter DDD válido e 10 ou 11 dígitos",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para telefone com mais de 11 dígitos")
    void deveLancarExcecaoParaTelefoneMaiorQue11Digitos() {
        final Telefone telefone = Telefone.from("119999988888");

        final TelefoneValidator validator = new TelefoneValidator(
                telefone,
                new ThrowsValidationHandler()
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "Telefone deve conter DDD válido e 10 ou 11 dígitos",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para telefone com caracteres inválidos")
    void deveLancarExcecaoParaTelefoneComCaracteresInvalidos() {
        final Telefone telefone = Telefone.from("11A99998888");

        final TelefoneValidator validator = new TelefoneValidator(
                telefone,
                new ThrowsValidationHandler()
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "Telefone deve conter DDD válido e 10 ou 11 dígitos",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para DDD menor que 11")
    void deveLancarExcecaoParaDddMenorQue11() {
        final Telefone telefone = Telefone.from("1033334444");

        final TelefoneValidator validator = new TelefoneValidator(
                telefone,
                new ThrowsValidationHandler()
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "Telefone deve conter DDD válido e 10 ou 11 dígitos",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para DDD maior que 99")
    void deveLancarExcecaoParaDddMaiorQue99() {
        final Telefone telefone = Telefone.from("10099998888");

        final TelefoneValidator validator = new TelefoneValidator(
                telefone,
                new ThrowsValidationHandler()
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "Telefone deve conter DDD válido e 10 ou 11 dígitos",
                exception.getErrors().getFirst().message()
        );
    }
}
