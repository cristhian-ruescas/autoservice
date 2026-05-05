package com.autoservice.domain.pessoa.valueobject;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Telefone - Value Object")
class TelefoneTest {

    @Test
    @DisplayName("Deve criar telefone válido com sucesso")
    void deveCriarTelefoneValido() {
        assertDoesNotThrow(() -> Telefone.from("(11) 99999-9999"));
    }

    @Test
    @DisplayName("Deve normalizar telefone removendo caracteres especiais")
    void deveNormalizarTelefone() {
        final Telefone telefone = Telefone.from("(11) 99999-9999");

        assertEquals("11999999999", telefone.getValue());
    }

    @Test
    @DisplayName("Deve lançar erro ao validar telefone inválido")
    void deveRejeitarTelefoneInvalido() {
        final Telefone telefone = Telefone.from("123");

        final ThrowsValidationHandler handler = new ThrowsValidationHandler();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> telefone.validate(handler)
        );

        assertEquals(
                "Telefone deve conter DDD válido e 10 ou 11 dígitos",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar erro ao validar telefone nulo")
    void deveRejeitarTelefoneNulo() {
        final Telefone telefone = Telefone.from(null);

        final ThrowsValidationHandler handler = new ThrowsValidationHandler();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> telefone.validate(handler)
        );

        assertEquals(
                "Telefone não deve ser nulo ou vazio",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar erro ao validar telefone vazio")
    void deveRejeitarTelefoneVazio() {
        final Telefone telefone = Telefone.from("");

        final ThrowsValidationHandler handler = new ThrowsValidationHandler();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> telefone.validate(handler)
        );

        assertEquals(
                "Telefone não deve ser nulo ou vazio",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve retornar valor corretamente no toString")
    void deveRetornarValorNoToString() {
        final Telefone telefone = Telefone.from("(11) 99999-9999");

        assertEquals("11999999999", telefone.toString());
    }

    @Test
    @DisplayName("Deve ser igual quando valores forem iguais")
    void deveSerIgualQuandoValoresForemIguais() {
        final Telefone t1 = Telefone.from("(11) 99999-9999");
        final Telefone t2 = Telefone.from("11999999999");

        assertEquals(t1, t2);
    }

    @Test
    @DisplayName("Deve ser diferente quando valores forem diferentes")
    void deveSerDiferenteQuandoValoresForemDiferentes() {
        final Telefone t1 = Telefone.from("11999999999");
        final Telefone t2 = Telefone.from("11888888888");

        assertNotEquals(t1, t2);
    }

    @Test
    @DisplayName("Deve retornar false ao comparar com null")
    void deveSerDiferenteDeNull() {
        final Telefone telefone = Telefone.from("11999999999");

        assertFalse(telefone.equals(null));
    }

    @Test
    @DisplayName("Deve retornar false ao comparar com outro tipo")
    void deveSerDiferenteDeOutroTipo() {
        final Telefone telefone = Telefone.from("11999999999");

        assertFalse(telefone.equals("11999999999"));
    }

    @Test
    @DisplayName("Deve gerar hashCode consistente para valores iguais")
    void deveTerHashCodeConsistente() {
        final Telefone t1 = Telefone.from("11999999999");
        final Telefone t2 = Telefone.from("(11) 99999-9999");

        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    @DisplayName("Deve permitir criação via construtor protegido (JPA)")
    void devePermitirConstrutorProtegido() throws Exception {
        final var constructor = Telefone.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final Telefone telefone = constructor.newInstance();

        assertNull(telefone.getValue());
    }
}
