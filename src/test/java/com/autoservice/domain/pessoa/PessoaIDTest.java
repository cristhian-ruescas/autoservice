package com.autoservice.domain.pessoa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PessoaID")
class PessoaIDTest {

    @Test
    @DisplayName("Deve testar construtor protegido para JPA")
    void deveTestarConstrutorProtegidoParaJPA() throws Exception {
        final Constructor<PessoaID> constructor =
                PessoaID.class.getDeclaredConstructor();

        constructor.setAccessible(true);

        final PessoaID pessoaID = constructor.newInstance();

        assertNotNull(pessoaID);
        assertNull(pessoaID.getValue());
    }

    @Test
    @DisplayName("Deve gerar um ID único válido")
    void deveGerarIdUnicoValido() {
        final PessoaID pessoaID = PessoaID.unique();

        assertNotNull(pessoaID);
        assertNotNull(pessoaID.getValue());
        assertFalse(pessoaID.getValue().isBlank());
    }

    @Test
    @DisplayName("Deve criar PessoaID a partir de String")
    void deveCriarPessoaIdAPartirDeString() {
        final String id = "abc-123";

        final PessoaID pessoaID = PessoaID.from(id);

        assertNotNull(pessoaID);
        assertEquals(id, pessoaID.getValue());
    }

    @Test
    @DisplayName("Deve criar PessoaID a partir de UUID")
    void deveCriarPessoaIdAPartirDeUuid() {
        final UUID uuid = UUID.randomUUID();

        final PessoaID pessoaID = PessoaID.from(uuid);

        assertNotNull(pessoaID);
        assertEquals(
                uuid.toString().toLowerCase(),
                pessoaID.getValue()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar PessoaID com String nula")
    void deveLancarExcecaoAoCriarPessoaIdComStringNula() {
        assertThrows(
                NullPointerException.class,
                () -> PessoaID.from((String) null)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar PessoaID com UUID nulo")
    void deveLancarExcecaoAoCriarPessoaIdComUuidNulo() {
        assertThrows(
                NullPointerException.class,
                () -> PessoaID.from((UUID) null)
        );
    }

    @Test
    @DisplayName("Deve comparar igualdade corretamente")
    void deveCompararIgualdadeCorretamente() {
        final String id = "mesmo-id";

        final PessoaID pessoaID1 = PessoaID.from(id);
        final PessoaID pessoaID2 = PessoaID.from(id);

        assertEquals(pessoaID1, pessoaID2);
        assertEquals(
                pessoaID1.hashCode(),
                pessoaID2.hashCode()
        );
    }

    @Test
    @DisplayName("Deve retornar falso ao comparar IDs diferentes")
    void deveRetornarFalsoAoCompararIdsDiferentes() {
        final PessoaID pessoaID1 = PessoaID.from("id-1");
        final PessoaID pessoaID2 = PessoaID.from("id-2");

        assertNotEquals(pessoaID1, pessoaID2);
    }

    @Test
    @DisplayName("Deve retornar falso ao comparar com null")
    void deveRetornarFalsoAoCompararComNull() {
        final PessoaID pessoaID = PessoaID.from("id");

        assertFalse(pessoaID.equals(null));
    }

    @Test
    @DisplayName("Deve retornar falso ao comparar com outro tipo")
    void deveRetornarFalsoAoCompararComOutroTipo() {
        final PessoaID pessoaID = PessoaID.from("id");

        assertFalse(pessoaID.equals("id"));
    }
}
