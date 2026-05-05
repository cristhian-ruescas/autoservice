package com.autoservice.domain.veiculo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VeiculoID")
class VeiculoIDTest {

    @Test
    @DisplayName("Deve testar construtor protegido para JPA")
    void deveTestarConstrutorProtegidoParaJPA() throws Exception {
        final Constructor<VeiculoID> constructor =
                VeiculoID.class.getDeclaredConstructor();

        constructor.setAccessible(true);

        final VeiculoID id = constructor.newInstance();

        assertNotNull(id);
        assertNull(id.getValue());
    }

    @Test
    @DisplayName("Deve gerar ID único válido")
    void deveGerarIdUnicoValido() {
        final VeiculoID id = VeiculoID.unique();

        assertNotNull(id);
        assertNotNull(id.getValue());
        assertFalse(id.getValue().isBlank());
    }

    @Test
    @DisplayName("Deve criar VeiculoID a partir de String")
    void deveCriarVeiculoIdAPartirDeString() {
        final String valor = "abc-123";

        final VeiculoID id = VeiculoID.from(valor);

        assertNotNull(id);
        assertEquals(valor, id.getValue());
    }

    @Test
    @DisplayName("Deve criar VeiculoID a partir de UUID")
    void deveCriarVeiculoIdAPartirDeUUID() {
        final UUID uuid = UUID.randomUUID();

        final VeiculoID id = VeiculoID.from(uuid);

        assertNotNull(id);
        assertEquals(
                uuid.toString().toLowerCase(),
                id.getValue()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar VeiculoID com valor nulo")
    void deveLancarExcecaoQuandoValorForNulo() {
        assertThrows(
                NullPointerException.class,
                () -> VeiculoID.from((String) null)
        );
    }

    @Test
    @DisplayName("Deve comparar igualdade corretamente")
    void deveCompararIgualdadeCorretamente() {
        final String valor = "id-teste";

        final VeiculoID id1 = VeiculoID.from(valor);
        final VeiculoID id2 = VeiculoID.from(valor);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar falso para valores diferentes")
    void deveRetornarFalsoParaValoresDiferentes() {
        final VeiculoID id1 = VeiculoID.from("id-1");
        final VeiculoID id2 = VeiculoID.from("id-2");

        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("Deve retornar falso ao comparar com null")
    void deveRetornarFalsoAoCompararComNull() {
        final VeiculoID id = VeiculoID.from("id");

        assertFalse(id.equals(null));
    }

    @Test
    @DisplayName("Deve retornar falso ao comparar com outro tipo")
    void deveRetornarFalsoAoCompararComOutroTipo() {
        final VeiculoID id = VeiculoID.from("id");

        assertFalse(id.equals("id"));
    }
}