package com.autoservice.domain.pessoa.validators;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.domain.pessoa.valueobject.CNPJ;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("PessoaJuridicaValidator")
class PessoaJuridicaValidatorTest {

    private static final String CNPJ_VALIDO        = "11222333000181";
    private static final String EMAIL_VALIDO       = "contato@empresa.com";
    private static final String TELEFONE_VALIDO    = "11999999999";
    private static final String RAZAO_SOCIAL_VALIDA = "Empresa Teste Ltda";

    @Test
    @DisplayName("Deve validar pessoa jurídica válida com sucesso")
    void deveValidarPessoaJuridicaValida() {
        PessoaJuridica.newPessoaJuridica(
                Email.from(EMAIL_VALIDO),
                Telefone.from(TELEFONE_VALIDO),
                RAZAO_SOCIAL_VALIDA,
                CNPJ.from(CNPJ_VALIDO),
                PessoaID.unique()
        );
    }

    @Test
    @DisplayName("Deve rejeitar pessoa jurídica com razão social nula")
    void deveRejeitarRazaoSocialNula() {
        var exception = assertThrows(DomainException.class, () ->
                PessoaJuridica.newPessoaJuridica(
                        Email.from(EMAIL_VALIDO),
                        Telefone.from(TELEFONE_VALIDO),
                        null,
                        CNPJ.from(CNPJ_VALIDO),
                        PessoaID.unique()
                )
        );

        assertEquals(
                "Razão Social não pode ser nula ou vazia",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve rejeitar pessoa jurídica com razão social em branco")
    void deveRejeitarRazaoSocialEmBranco() {
        var exception = assertThrows(DomainException.class, () ->
                PessoaJuridica.newPessoaJuridica(
                        Email.from(EMAIL_VALIDO),
                        Telefone.from(TELEFONE_VALIDO),
                        "   ",
                        CNPJ.from(CNPJ_VALIDO),
                        PessoaID.unique()
                )
        );

        assertEquals(
                "Razão Social não pode ser nula ou vazia",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve rejeitar pessoa jurídica com razão social muito curta")
    void deveRejeitarRazaoSocialMuitoCurta() {
        var exception = assertThrows(DomainException.class, () ->
                PessoaJuridica.newPessoaJuridica(
                        Email.from(EMAIL_VALIDO),
                        Telefone.from(TELEFONE_VALIDO),
                        "AB",
                        CNPJ.from(CNPJ_VALIDO),
                        PessoaID.unique()
                )
        );

        assertEquals(
                "Razão Social deve possuir entre 3 e 255 caracteres",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve rejeitar pessoa jurídica com razão social muito longa")
    void deveRejeitarRazaoSocialMuitoLonga() {
        final String razaoSocialLonga = "a".repeat(256);

        var exception = assertThrows(DomainException.class, () ->
                PessoaJuridica.newPessoaJuridica(
                        Email.from(EMAIL_VALIDO),
                        Telefone.from(TELEFONE_VALIDO),
                        razaoSocialLonga,
                        CNPJ.from(CNPJ_VALIDO),
                        PessoaID.unique()
                )
        );

        assertEquals(
                "Razão Social deve possuir entre 3 e 255 caracteres",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve aceitar CNPJ nulo na validação")
    void deveAceitarCnpjNulo() {
        PessoaJuridica.newPessoaJuridica(
                Email.from(EMAIL_VALIDO),
                Telefone.from(TELEFONE_VALIDO),
                RAZAO_SOCIAL_VALIDA,
                null,
                PessoaID.unique()
        );
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ inválido na validação")
    void deveRejeitarCnpjInvalido() {
        assertThrows(DomainException.class, () ->
                PessoaJuridica.newPessoaJuridica(
                        Email.from(EMAIL_VALIDO),
                        Telefone.from(TELEFONE_VALIDO),
                        RAZAO_SOCIAL_VALIDA,
                        CNPJ.from("11111111111111"),
                        PessoaID.unique()
                )
        );
    }
}
