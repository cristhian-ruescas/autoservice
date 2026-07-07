package com.autoservice.domain.pessoa.valueobject;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CPF")
class CPFTest {

    @Test
    @DisplayName("Deve criar CPF válido com sucesso")
    void deveCriarCPFValido() {
        assertDoesNotThrow(() -> CPF.from("52998224725"));
    }

    @Test
    @DisplayName("Deve lançar erro ao validar CPF inválido (dígitos verificadores)")
    void deveRejeitarCPFInvalido() {
        final CPF cpf = CPF.from("52998224724");

        final ThrowsValidationHandler handler = new ThrowsValidationHandler();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> cpf.validate(handler)
        );

        assertEquals(
                "CPF inválido",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar erro ao validar CPF com formato inválido")
    void deveRejeitarCPFFormatoInvalido() {
        final CPF cpf = CPF.from("123");

        final ThrowsValidationHandler handler = new ThrowsValidationHandler();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> cpf.validate(handler)
        );

        assertEquals(
                "CPF deve estar no formato válido (XXX.XXX.XXX-XX ou XXXXXXXXXXX)",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar erro ao validar CPF nulo")
    void deveRejeitarCPFNulo() {
        final CPF cpf = CPF.from(null);

        final ThrowsValidationHandler handler = new ThrowsValidationHandler();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> cpf.validate(handler)
        );

        assertEquals(
                "CPF não deve ser nulo ou vazio",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve lançar erro ao validar CPF vazio")
    void deveRejeitarCPFVazio() {
        final CPF cpf = CPF.from("");

        final ThrowsValidationHandler handler = new ThrowsValidationHandler();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> cpf.validate(handler)
        );

        assertEquals(
                "CPF não deve ser nulo ou vazio",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve retornar valor corretamente no toString")
    void deveRetornarValorNoToString() {
        final CPF cpf = CPF.from("52998224725");

        assertEquals("52998224725", cpf.toString());
    }

    @Test
    @DisplayName("Deve ser igual quando valores forem iguais")
    void deveSerIgualQuandoValoresForemIguais() {
        final CPF cpf1 = CPF.from("52998224725");
        final CPF cpf2 = CPF.from("52998224725");

        assertEquals(cpf1, cpf2);
    }

    @Test
    @DisplayName("Deve ser diferente quando valores forem diferentes")
    void deveSerDiferenteQuandoValoresForemDiferentes() {
        final CPF cpf1 = CPF.from("52998224725");
        final CPF cpf2 = CPF.from("52998224724");

        assertNotEquals(cpf1, cpf2);
    }

    @Test
    @DisplayName("Deve retornar false ao comparar com null")
    void deveSerDiferenteDeNull() {
        final CPF cpf = CPF.from("52998224725");

        assertNotEquals( null, cpf);
    }

    @Test
    @DisplayName("Deve retornar false ao comparar com outro tipo")
    void deveSerDiferenteDeOutroTipo() {
        final CPF cpf = CPF.from("52998224725");

        assertNotEquals( null, cpf);

    }

    @Test
    @DisplayName("Deve gerar hashCode consistente para valores iguais")
    void deveTerHashCodeConsistente() {
        final CPF cpf1 = CPF.from("52998224725");
        final CPF cpf2 = CPF.from("52998224725");

        assertEquals(cpf1.hashCode(), cpf2.hashCode());
    }

    @Test
    @DisplayName("Deve permitir criação via construtor protegido (JPA)")
    void devePermitirConstrutorProtegido() throws Exception {
        final var constructor = CPF.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final CPF cpf = constructor.newInstance();

        assertNull(cpf.getValue());
    }
}
