package com.autoservice.domain.pessoa.validators;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.valueobject.CPF;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("PessoaFisicaValidator")
class PessoaFisicaValidatorTest {


    @Test
    @DisplayName("Deve rejeitar pessoa física com nome nulo")
    void deveRejeitarNomeNulo() {
        var exception = assertThrows(DomainException.class, () ->
                PessoaFisica.newPessoaFisica(
                        null,
                        null,
                        null,
                        CPF.from("52998224725")
                )
        );

        assertEquals(
                "Nome não pode ser vazio",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve rejeitar pessoa física com nome em branco")
    void deveRejeitarNomeEmBranco() {
        var exception = assertThrows(DomainException.class, () ->
                PessoaFisica.newPessoaFisica(
                        null,
                        null,
                        "   ",
                        CPF.from("52998224725")
                )
        );

        assertEquals(
                "Nome não pode ser vazio",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve rejeitar pessoa física com nome muito curto")
    void deveRejeitarNomeMuitoCurto() {
        var exception = assertThrows(DomainException.class, () ->
                PessoaFisica.newPessoaFisica(
                        null,
                        null,
                        "Jo",
                        CPF.from("52998224725")
                )
        );

        assertEquals(
                "Nome deve possuir entre 3 e 255 caracteres",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve rejeitar pessoa física com nome muito longo")
    void deveRejeitarNomeMuitoLongo() {
        final String nomeGrande = "a".repeat(256);

        var exception = assertThrows(DomainException.class, () ->
                PessoaFisica.newPessoaFisica(
                        null,
                        null,
                        nomeGrande,
                        CPF.from("52998224725")
                )
        );

        assertEquals(
                "Nome deve possuir entre 3 e 255 caracteres",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    @DisplayName("Deve rejeitar CPF inválido na validação")
    void deveRejeitarCpfInvalido() {
        assertThrows(DomainException.class, () ->
                PessoaFisica.newPessoaFisica(
                        null,
                        null,
                        "Carlos Silva",
                        CPF.from("11111111111")
                )
        );
    }
}
