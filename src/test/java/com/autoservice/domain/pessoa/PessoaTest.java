package com.autoservice.domain.pessoa;

import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import com.autoservice.validation.ValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pessoa")
class PessoaTest {

    @Test
    @DisplayName("Deve testar construtor protegido para JPA")
    void deveTestarConstrutorProtegidoParaJPA() throws Exception {
        final Constructor<PessoaFake> constructor =
                PessoaFake.class.getDeclaredConstructor();

        constructor.setAccessible(true);

        final PessoaFake pessoa = constructor.newInstance();

        assertNull(pessoa.getId());
        assertNull(pessoa.getEmail());
        assertNull(pessoa.getTelefone());
    }

    @Test
    @DisplayName("Deve criar pessoa com construtor parametrizado")
    void deveCriarPessoaComConstrutorParametrizado() {
        final PessoaID pessoaId = PessoaID.unique();
        final Email email = Email.from("teste@email.com");
        final Telefone telefone = Telefone.from("11999999999");

        final PessoaFake pessoa = new PessoaFake(
                pessoaId,
                email,
                telefone
        );

        assertNotNull(pessoa);
        assertEquals(pessoaId, pessoa.getId());
        assertEquals(email, pessoa.getEmail());
        assertEquals(telefone, pessoa.getTelefone());
    }

    @Test
    @DisplayName("Deve permitir criar pessoa com email e telefone nulos")
    void devePermitirCriarPessoaComEmailETelefoneNulos() {
        final PessoaID pessoaId = PessoaID.unique();

        final PessoaFake pessoa = new PessoaFake(
                pessoaId,
                null,
                null
        );

        assertNotNull(pessoa);
        assertEquals(pessoaId, pessoa.getId());
        assertNull(pessoa.getEmail());
        assertNull(pessoa.getTelefone());
    }

    private static class PessoaFake extends Pessoa {

        protected PessoaFake() {
            super();
        }

        protected PessoaFake(
                final PessoaID id,
                final Email email,
                final Telefone telefone
        ) {
            super(id, email, telefone);
        }

        @Override
        public void validate(final ValidationHandler handler) {
        }
    }
}
