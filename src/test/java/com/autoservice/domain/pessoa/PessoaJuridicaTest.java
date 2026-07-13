package com.autoservice.domain.pessoa;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.valueobject.CNPJ;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PessoaJuridica")
class PessoaJuridicaTest {

    private static final String CNPJ_VALIDO = "11222333000181";
    private static final String EMAIL_VALIDO = "contato@empresa.com";
    private static final String TELEFONE_VALIDO = "11999999999";
    private static final String RAZAO_SOCIAL_VALIDA = "Empresa Teste Ltda";

    @Test
    @DisplayName("Deve testar construtor protegido para JPA")
    void deveTestarConstrutorProtegidoParaJPA() throws Exception {
        final Constructor<PessoaJuridica> constructor =
                PessoaJuridica.class.getDeclaredConstructor();

        constructor.setAccessible(true);

        final PessoaJuridica pj = constructor.newInstance();

        assertNotNull(pj);
        assertNull(pj.getId());
        assertNull(pj.getEmail());
        assertNull(pj.getTelefone());
        assertNull(pj.getRazaoSocial());
        assertNull(pj.getCnpj());
        assertNull(pj.getRepresentanteLegalId());
    }

    @Test
    @DisplayName("Deve criar nova pessoa jurídica válida com todos os campos")
    void deveCriarNovaPessoaJuridicaValida() {
        final Email email = Email.from(EMAIL_VALIDO);
        final Telefone telefone = Telefone.from(TELEFONE_VALIDO);
        final CNPJ cnpj = CNPJ.from(CNPJ_VALIDO);
        final PessoaID representante = PessoaID.unique();

        final PessoaJuridica pj = PessoaJuridica.newPessoaJuridica(
                email,
                telefone,
                RAZAO_SOCIAL_VALIDA,
                cnpj,
                representante
        );

        assertNotNull(pj);
        assertNotNull(pj.getId());
        assertEquals(email, pj.getEmail());
        assertEquals(telefone, pj.getTelefone());
        assertEquals(RAZAO_SOCIAL_VALIDA, pj.getRazaoSocial());
        assertEquals(cnpj, pj.getCnpj());
        assertEquals(representante, pj.getRepresentanteLegalId());
    }

    @Test
    @DisplayName("Deve gerar IDs diferentes a cada criação")
    void deveGerarIdsDiferentesEmCriacoesDistintas() {
        final PessoaJuridica pj1 = criarPessoaJuridicaValida();
        final PessoaJuridica pj2 = criarPessoaJuridicaValida();

        assertNotEquals(pj1.getId(), pj2.getId());
    }

    @Test
    @DisplayName("Deve criar pessoa jurídica com campos opcionais nulos")
    void deveCriarPessoaJuridicaComCamposOpcionaisNulos() {
        final CNPJ cnpj = CNPJ.from(CNPJ_VALIDO);
        final PessoaID representante = PessoaID.unique();

        final PessoaJuridica pj = PessoaJuridica.newPessoaJuridica(
                null,
                null,
                RAZAO_SOCIAL_VALIDA,
                cnpj,
                representante
        );

        assertNotNull(pj);
        assertNull(pj.getEmail());
        assertNull(pj.getTelefone());
        assertEquals(RAZAO_SOCIAL_VALIDA, pj.getRazaoSocial());
    }

    @Test
    @DisplayName("Deve criar pessoa jurídica com ID existente usando withId")
    void deveCriarPessoaJuridicaComIdExistente() {
        final PessoaID id = PessoaID.unique();
        final Email email = Email.from(EMAIL_VALIDO);
        final Telefone telefone = Telefone.from(TELEFONE_VALIDO);
        final CNPJ cnpj = CNPJ.from(CNPJ_VALIDO);
        final PessoaID representante = PessoaID.unique();

        final PessoaJuridica pj = PessoaJuridica.withId(
                id,
                email,
                telefone,
                RAZAO_SOCIAL_VALIDA,
                cnpj,
                representante
        );

        assertNotNull(pj);
        assertEquals(id, pj.getId());
        assertEquals(email, pj.getEmail());
        assertEquals(telefone, pj.getTelefone());
        assertEquals(RAZAO_SOCIAL_VALIDA, pj.getRazaoSocial());
        assertEquals(cnpj, pj.getCnpj());
        assertEquals(representante, pj.getRepresentanteLegalId());
    }

    @Test
    @DisplayName("Deve preservar ID fornecido no withId")
    void devePreservarIdFornecidoNoWithId() {
        final PessoaID idEsperado = PessoaID.from("id-fixo-para-teste");

        final PessoaJuridica pj = PessoaJuridica.withId(
                idEsperado,
                Email.from(EMAIL_VALIDO),
                Telefone.from(TELEFONE_VALIDO),
                RAZAO_SOCIAL_VALIDA,
                CNPJ.from(CNPJ_VALIDO),
                PessoaID.unique()
        );

        assertEquals(idEsperado.getValue(), pj.getId().getValue());
    }

    @Test
    @DisplayName("Deve validar pessoa jurídica válida sem exceção")
    void deveValidarPessoaJuridicaValida() {
        final PessoaJuridica pj = criarPessoaJuridicaValida();

        assertDoesNotThrow(() -> pj.validate(new ThrowsValidationHandler()));
    }

    @Test
    @DisplayName("Deve registrar erro quando razão social for nula")
    void deveRegistrarErroQuandoRazaoSocialForNula() {
        var erro = assertThrows(DomainException.class, () -> PessoaJuridica.newPessoaJuridica(
                Email.from(EMAIL_VALIDO),
                Telefone.from(TELEFONE_VALIDO),
                null,
                CNPJ.from(CNPJ_VALIDO),
                PessoaID.unique()
        ));

        assertTrue(erro.getErrors().stream()
                .anyMatch(e -> e.message().equals("Razão Social não pode ser nula ou vazia")));
    }

    @Test
    @DisplayName("Deve registrar erro quando razão social for vazia")
    void deveRegistrarErroQuandoRazaoSocialForVazia() {
        var erro = assertThrows(DomainException.class, () -> PessoaJuridica.newPessoaJuridica(
                Email.from(EMAIL_VALIDO),
                Telefone.from(TELEFONE_VALIDO),
                "   ",
                CNPJ.from(CNPJ_VALIDO),
                PessoaID.unique()
        ));

        assertTrue(erro.getErrors().stream()
                .anyMatch(e -> e.message().equals("Razão Social não pode ser nula ou vazia")));
    }

    @Test
    @DisplayName("Deve registrar erro quando razão social for menor que 3 caracteres")
    void deveRegistrarErroQuandoRazaoSocialForCurta() {
        var erro = assertThrows(DomainException.class, () -> PessoaJuridica.newPessoaJuridica(
                Email.from(EMAIL_VALIDO),
                Telefone.from(TELEFONE_VALIDO),
                "AB",
                CNPJ.from(CNPJ_VALIDO),
                PessoaID.unique()
        ));

        assertTrue(erro.getErrors().stream()
                .anyMatch(e -> e.message().equals("Razão Social deve possuir entre 3 e 255 caracteres")));
    }

    @Test
    @DisplayName("Deve registrar erro quando razão social for maior que 255 caracteres")
    void deveRegistrarErroQuandoRazaoSocialForLonga() {
        final String razaoSocialLonga = "A".repeat(256);

        var erro = assertThrows(DomainException.class, () -> PessoaJuridica.newPessoaJuridica(
                Email.from(EMAIL_VALIDO),
                Telefone.from(TELEFONE_VALIDO),
                razaoSocialLonga,
                CNPJ.from(CNPJ_VALIDO),
                PessoaID.unique()
        ));

        assertTrue(erro.getErrors().stream()
                .anyMatch(e -> e.message().equals("Razão Social deve possuir entre 3 e 255 caracteres")));
    }

    @Test
    @DisplayName("Deve aceitar razão social com 3 caracteres")
    void deveAceitarRazaoSocialCom3Caracteres() {
        assertDoesNotThrow(() -> PessoaJuridica.newPessoaJuridica(
                Email.from(EMAIL_VALIDO),
                Telefone.from(TELEFONE_VALIDO),
                "ABC",
                CNPJ.from(CNPJ_VALIDO),
                PessoaID.unique()
        ));
    }

    @Test
    @DisplayName("Deve aceitar razão social com 255 caracteres")
    void deveAceitarRazaoSocialCom255Caracteres() {
        final String razao = "A".repeat(255);

        assertDoesNotThrow(() -> PessoaJuridica.newPessoaJuridica(
                Email.from(EMAIL_VALIDO),
                Telefone.from(TELEFONE_VALIDO),
                razao,
                CNPJ.from(CNPJ_VALIDO),
                PessoaID.unique()
        ));
    }

    @Test
    @DisplayName("Deve registrar erro quando representante legal for nulo")
    void deveRegistrarErroQuandoRepresentanteLegalForNulo() {
        var erro = assertThrows(DomainException.class, () -> PessoaJuridica.newPessoaJuridica(
                Email.from(EMAIL_VALIDO),
                Telefone.from(TELEFONE_VALIDO),
                RAZAO_SOCIAL_VALIDA,
                CNPJ.from(CNPJ_VALIDO),
                null
        ));

        assertTrue(erro.getErrors().stream()
                .anyMatch(e -> e.message().equals("Representante Legal não deve ser nulo")));
    }

    @Test
    @DisplayName("Deve criar pessoa jurídica com CNPJ nulo")
    void deveCriarPessoaJuridicaComCnpjNulo() {
        final PessoaJuridica pj = PessoaJuridica.newPessoaJuridica(
                Email.from(EMAIL_VALIDO),
                Telefone.from(TELEFONE_VALIDO),
                RAZAO_SOCIAL_VALIDA,
                null,
                PessoaID.unique()
        );

        assertDoesNotThrow(() -> pj.validate(new ThrowsValidationHandler()));
        assertNull(pj.getCnpj());
    }

    @Test
    @DisplayName("Deve acumular múltiplos erros de validação")
    void deveAcumularMultiplosErros() {
        var erro = assertThrows(DomainException.class, () -> PessoaJuridica.newPessoaJuridica(
                Email.from(EMAIL_VALIDO),
                Telefone.from(TELEFONE_VALIDO),
                null,
                CNPJ.from(CNPJ_VALIDO),
                null
        ));

        assertEquals(2, erro.getErrors().size());
    }

    private PessoaJuridica criarPessoaJuridicaValida() {
        return PessoaJuridica.newPessoaJuridica(
                Email.from(EMAIL_VALIDO),
                Telefone.from(TELEFONE_VALIDO),
                RAZAO_SOCIAL_VALIDA,
                CNPJ.from(CNPJ_VALIDO),
                PessoaID.unique()
        );
    }
}
