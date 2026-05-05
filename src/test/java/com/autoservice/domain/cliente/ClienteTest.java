package com.autoservice.domain.cliente;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.domain.events.DomainEvent;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cliente")
class ClienteTest {

    @Test
    @DisplayName("Deve cobrir construtor protegido do JPA")
    void deveCobrirConstrutorProtegido() throws Exception {
        final var constructor = Cliente.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var cliente = constructor.newInstance();

        assertNull(cliente.getId());
        assertNull(cliente.getPessoaId());
        assertNull(cliente.getDataCadastro());
    }

    @Test
    @DisplayName("Deve criar cliente válido com sucesso")
    void deveCriarClienteValido() {
        final var pessoaId = PessoaID.unique();

        final var cliente = Cliente.newCliente(pessoaId);

        assertNotNull(cliente);
        assertNotNull(cliente.getId());
        assertNotNull(cliente.getId().getValue());
        assertEquals(pessoaId, cliente.getPessoaId());
        assertEquals(LocalDate.now(), cliente.getDataCadastro().getValue());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente com PessoaID nulo")
    void deveLancarExcecaoComPessoaIdNulo() {
        final var exception = assertThrows(
                DomainException.class,
                () -> Cliente.newCliente(null)
        );

        assertEquals(
                "'pessoaId' não deve ser nulo",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve restaurar cliente corretamente usando método with")
    void deveRestaurarClienteComWith() {
        final var clienteId = ClienteID.unique();
        final var pessoaId = PessoaID.unique();

        final var cliente = Cliente.with(clienteId, pessoaId, LocalDate.now());

        assertNotNull(cliente);
        assertEquals(clienteId, cliente.getId());
        assertEquals(pessoaId, cliente.getPessoaId());
        assertEquals(LocalDate.now(), cliente.getDataCadastro().getValue());
    }

    @Test
    @DisplayName("Deve validar cliente com sucesso")
    void deveValidarClienteComSucesso() {
        final var pessoaId = PessoaID.unique();
        final var cliente = Cliente.with(
                ClienteID.unique(),
                pessoaId,
                LocalDate.now()
        );

        assertDoesNotThrow(() ->
                cliente.validate(new ThrowsValidationHandler())
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar cliente sem PessoaID")
    void deveLancarExcecaoAoValidarSemPessoaId() {
        final var cliente = Cliente.with(
                ClienteID.unique(),
                null,
                LocalDate.now()
        );

        final var exception = assertThrows(
                DomainException.class,
                () -> cliente.validate(new ThrowsValidationHandler())
        );

        assertEquals(
                "'pessoaId' não deve ser nulo",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("registerEvent(null) não adiciona evento")
    void registerEventNullIgnorado() throws Exception {
        final var cliente = Cliente.with(
                ClienteID.unique(),
                PessoaID.unique(),
                LocalDate.now()
        );

        Method m = AggregateRoot.class.getDeclaredMethod("registerEvent", DomainEvent.class);
        m.setAccessible(true);
        m.invoke(cliente, (Object) null);

        assertTrue(cliente.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Deve permitir criação via with sem validação imediata")
    void devePermitirCriacaoComWithSemValidacaoImediata() {
        final var clienteId = ClienteID.unique();

        final var cliente = Cliente.with(
                clienteId,
                null,
                LocalDate.now()
        );

        assertNotNull(cliente);
        assertEquals(clienteId, cliente.getId());
        assertNull(cliente.getPessoaId());
    }
}