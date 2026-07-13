package com.autoservice.domain.pessoa.valueobject;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Email")
class EmailTest {

    @Test
    @DisplayName("Deve criar email válido com sucesso")
    void deveCriarEmailValido() {
        assertDoesNotThrow(() -> Email.from("usuario@email.com"));
    }

    @Test
    @DisplayName("Deve normalizar email para minúsculo e sem espaços")
    void deveNormalizarEmail() {
        final Email email = Email.from("  USUARIO@EMAIL.COM  ");

        assertEquals("usuario@email.com", email.getValue());
    }

    @Test
    @DisplayName("Deve lançar erro ao validar email inválido")
    void deveRejeitarEmailInvalido() {
        final Email email = Email.from("email-invalido");

        final ThrowsValidationHandler handler = new ThrowsValidationHandler();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> email.validate(handler)
        );

        assertEquals(
                "Email deve estar em formato válido (ex: usuario@dominio.com)",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar erro ao validar email nulo")
    void deveRejeitarEmailNulo() {
        final Email email = Email.from(null);

        final ThrowsValidationHandler handler = new ThrowsValidationHandler();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> email.validate(handler)
        );

        assertEquals(
                "Email não deve ser nulo ou vazio",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar erro ao validar email vazio")
    void deveRejeitarEmailVazio() {
        final Email email = Email.from("");

        final ThrowsValidationHandler handler = new ThrowsValidationHandler();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> email.validate(handler)
        );

        assertEquals(
                "Email não deve ser nulo ou vazio",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve retornar valor corretamente no toString")
    void deveRetornarValorNoToString() {
        final Email email = Email.from("usuario@email.com");

        assertEquals("usuario@email.com", email.toString());
    }

    @Test
    @DisplayName("Deve ser igual quando valores forem iguais")
    void deveSerIgualQuandoValoresForemIguais() {
        final Email email1 = Email.from("usuario@email.com");
        final Email email2 = Email.from("usuario@email.com");

        assertEquals(email1, email2);
    }

    @Test
    @DisplayName("Deve ser diferente quando valores forem diferentes")
    void deveSerDiferenteQuandoValoresForemDiferentes() {
        final Email email1 = Email.from("usuario@email.com");
        final Email email2 = Email.from("outro@email.com");

        assertNotEquals(email1, email2);
    }

    @Test
    @DisplayName("Deve retornar false ao comparar com null")
    void deveSerDiferenteDeNull() {
        final Email email = Email.from("usuario@email.com");

        assertNotEquals(email, null);
    }

    @Test
    @DisplayName("Deve retornar false ao comparar com outro tipo")
    void deveSerDiferenteDeOutroTipo() {
        final Email email = Email.from("usuario@email.com");
        final Object outroTipo = "usuario@email.com";

        assertNotEquals(email, outroTipo);
    }

    @Test
    @DisplayName("Deve gerar hashCode consistente para valores iguais")
    void deveTerHashCodeConsistente() {
        final Email email1 = Email.from("usuario@email.com");
        final Email email2 = Email.from("usuario@email.com");

        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    @DisplayName("Deve permitir criação via construtor protegido (JPA)")
    void devePermitirConstrutorProtegido() throws Exception {
        final var constructor = Email.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final Email email = constructor.newInstance();

        assertNull(email.getValue());
    }
}
