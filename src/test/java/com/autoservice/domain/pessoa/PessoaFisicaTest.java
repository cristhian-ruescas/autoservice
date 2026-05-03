package com.autoservice.domain.pessoa;

import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PessoaFisica")
class PessoaFisicaTest {

    @Test
    @DisplayName("Deve testar construtor protegido para JPA")
    void deveTestarConstrutorProtegidoParaJPA() throws Exception {
        final Constructor<PessoaFisica> constructor =
                PessoaFisica.class.getDeclaredConstructor();

        constructor.setAccessible(true);

        final PessoaFisica pessoaFisica = constructor.newInstance();

        assertNotNull(pessoaFisica);
        assertNull(pessoaFisica.getId());
        assertNull(pessoaFisica.getEmail());
        assertNull(pessoaFisica.getTelefone());
        assertNull(pessoaFisica.getNome());
        assertNull(pessoaFisica.getCpf());
    }

    @Test
    @DisplayName("Deve criar uma nova pessoa física válida")
    void deveCriarNovaPessoaFisicaValida() {
        final Email email = Email.from("teste@email.com");
        final Telefone telefone = Telefone.from("11999999999");
        final String nome = "João Silva";
        final CPF cpf = CPF.from("52998224725");

        final PessoaFisica pessoaFisica =
                PessoaFisica.newPessoaFisica(
                        email,
                        telefone,
                        nome,
                        cpf
                );

        assertNotNull(pessoaFisica);
        assertNotNull(pessoaFisica.getId());
        assertEquals(email, pessoaFisica.getEmail());
        assertEquals(telefone, pessoaFisica.getTelefone());
        assertEquals(nome, pessoaFisica.getNome());
        assertEquals(cpf, pessoaFisica.getCpf());
    }

    @Test
    @DisplayName("Deve criar pessoa física com ID existente usando withId")
    void deveCriarPessoaFisicaComIdExistenteUsandoWithId() {
        final PessoaID pessoaID = PessoaID.unique();
        final Email email = Email.from("teste@email.com");
        final Telefone telefone = Telefone.from("11999999999");
        final String nome = "Maria Souza";
        final CPF cpf = CPF.from("52998224725");

        final PessoaFisica pessoaFisica =
                PessoaFisica.withId(
                        pessoaID,
                        email,
                        telefone,
                        nome,
                        cpf
                );

        assertNotNull(pessoaFisica);
        assertEquals(pessoaID, pessoaFisica.getId());
        assertEquals(email, pessoaFisica.getEmail());
        assertEquals(telefone, pessoaFisica.getTelefone());
        assertEquals(nome, pessoaFisica.getNome());
        assertEquals(cpf, pessoaFisica.getCpf());
    }

    @Test
    @DisplayName("Deve executar validação sem lançar exceção para dados válidos")
    void deveExecutarValidacaoSemLancarExcecaoParaDadosValidos() {
        final PessoaFisica pessoaFisica =
                PessoaFisica.newPessoaFisica(
                        Email.from("teste@email.com"),
                        Telefone.from("11999999999"),
                        "Carlos Oliveira",
                        CPF.from("52998224725")
                );

        assertDoesNotThrow(
                () -> pessoaFisica.validate(
                        new ThrowsValidationHandler()
                )
        );
    }

    @Test
    @DisplayName("Deve permitir criação com campos opcionais nulos")
    void devePermitirCriacaoComCamposOpcionaisNulos() {
        final CPF cpf = CPF.from("52998224725");

        final PessoaFisica pessoaFisica =
                PessoaFisica.newPessoaFisica(
                        null,
                        null,
                        "Pedro Santos",
                        cpf
                );

        assertNotNull(pessoaFisica);
        assertNull(pessoaFisica.getEmail());
        assertNull(pessoaFisica.getTelefone());
        assertEquals("Pedro Santos", pessoaFisica.getNome());
        assertEquals(cpf, pessoaFisica.getCpf());
    }
}
