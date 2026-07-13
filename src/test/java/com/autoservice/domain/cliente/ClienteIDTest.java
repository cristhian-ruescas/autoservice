package com.autoservice.domain.cliente;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ClienteID")
class ClienteIDTest {

    @Test
    @DisplayName("Deve cobrir construtor protegido do JPA")
    void deveCobrirConstrutorProtegido() throws Exception {
        final var constructor = ClienteID.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var clienteID = constructor.newInstance();

        assertNull(clienteID.getValue());
    }

    @Test
    @DisplayName("Deve criar ClienteID único com sucesso")
    void deveCriarClienteIDUnico() {
        final var clienteID = ClienteID.unique();

        assertNotNull(clienteID);
        assertNotNull(clienteID.getValue());
        assertFalse(clienteID.getValue().isBlank());
    }

    @Test
    @DisplayName("Deve gerar ClienteIDs diferentes ao usar unique")
    void deveGerarClienteIDsDiferentes() {
        final var id1 = ClienteID.unique();
        final var id2 = ClienteID.unique();

        assertNotEquals(id1, id2);
        assertNotEquals(id1.getValue(), id2.getValue());
    }

    @Test
    @DisplayName("Deve criar ClienteID a partir de String válida")
    void deveCriarClienteIDApartirDeString() {
        final String valor = "123e4567-e89b-12d3-a456-426614174000";

        final var clienteID = ClienteID.from(valor);

        assertNotNull(clienteID);
        assertEquals(valor, clienteID.getValue());
    }

    @Test
    @DisplayName("Deve criar ClienteID a partir de UUID válido")
    void deveCriarClienteIDApartirDeUUID() {
        final UUID uuid = UUID.randomUUID();

        final var clienteID = ClienteID.from(uuid);

        assertNotNull(clienteID);
        assertEquals(uuid.toString().toLowerCase(), clienteID.getValue());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar ClienteID com String nula")
    void deveLancarExcecaoComStringNula() {
        assertThrows(
                NullPointerException.class,
                () -> ClienteID.from((String) null)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar ClienteID com UUID nulo")
    void deveLancarExcecaoComUUIDNulo() {
        assertThrows(
                NullPointerException.class,
                () -> ClienteID.from((UUID) null)
        );
    }

    @Test
    @DisplayName("Deve comparar ClienteIDs iguais corretamente")
    void deveCompararClienteIDsIguais() {
        final String valor = "123e4567-e89b-12d3-a456-426614174000";

        final var id1 = ClienteID.from(valor);
        final var id2 = ClienteID.from(valor);

        assertEquals(id1, id2);
    }

    @Test
    @DisplayName("Deve retornar falso ao comparar com null")
    void deveRetornarFalsoAoCompararComNull() {
        final var clienteID = ClienteID.unique();

        assertNotEquals( null, clienteID);
    }

    @Test
    @DisplayName("Deve retornar falso para ClienteIDs diferentes")
    void deveRetornarFalsoParaClienteIDsDiferentes() {
        final var id1 = ClienteID.from("123e4567-e89b-12d3-a456-426614174000");
        final var id2 = ClienteID.from("123e4567-e89b-12d3-a456-426614174001");

        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("Deve gerar hashCode consistente para ClienteIDs iguais")
    void deveGerarHashCodeConsistente() {
        final String valor = "123e4567-e89b-12d3-a456-426614174000";

        final var id1 = ClienteID.from(valor);
        final var id2 = ClienteID.from(valor);

        assertEquals(id1.hashCode(), id2.hashCode());
    }
}