package com.autoservice.domain.pessoa.validators;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EmailValidator")
class EmailValidatorTest {

    @Test
    @DisplayName("Deve validar email válido com sucesso")
    void deveValidarEmailValido() {
        final Email email = Email.from("usuario@email.com");

        final EmailValidator validator = new EmailValidator(
                email,
                new ThrowsValidationHandler()
        );

        assertDoesNotThrow(validator::validate);
    }

    @Test
    @DisplayName("Deve lançar exceção para email nulo")
    void deveLancarExcecaoParaEmailNulo() throws Exception {
        final var constructor = Email.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final Email email = constructor.newInstance();

        final EmailValidator validator = new EmailValidator(
                email,
                new ThrowsValidationHandler()
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "Email não deve ser nulo ou vazio",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para email vazio")
    void deveLancarExcecaoParaEmailVazio() {
        final Email email = Email.from("");

        final EmailValidator validator = new EmailValidator(
                email,
                new ThrowsValidationHandler()
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "Email não deve ser nulo ou vazio",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para email com tamanho acima do permitido")
    void deveLancarExcecaoParaEmailMuitoLongo() {
        final String emailLongo = "a".repeat(250) + "@aa.com";

        final Email email = Email.from(emailLongo);

        final EmailValidator validator = new EmailValidator(
                email,
                new ThrowsValidationHandler()
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "Email não deve exceder 255 caracteres",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para email com formato inválido")
    void deveLancarExcecaoParaEmailFormatoInvalido() {
        final Email email = Email.from("email-invalido");

        final EmailValidator validator = new EmailValidator(
                email,
                new ThrowsValidationHandler()
        );

        final DomainException exception = assertThrows(
                DomainException.class,
                validator::validate
        );

        assertEquals(
                "Email deve estar em formato válido (ex: usuario@dominio.com)",
                exception.getErrors().getFirst().message()
        );
    }
}
