package com.autoservice.domain.pessoa.validators;

import com.autoservice.domain.pessoa.Pessoa;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PessoaValidator")
class PessoaValidatorTest {

    private static class PessoaFake extends Pessoa {

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

    private static class PessoaValidatorFake extends PessoaValidator {

        private boolean validacaoEspecificaExecutada = false;

        protected PessoaValidatorFake(
                final Pessoa pessoa,
                final ValidationHandler handler
        ) {
            super(pessoa, handler);
        }

        @Override
        protected void validateSpecificFields() {
            validacaoEspecificaExecutada = true;
        }

        public boolean foiExecutadaValidacaoEspecifica() {
            return validacaoEspecificaExecutada;
        }
    }

    @Test
    @DisplayName("Deve executar validação de campos específicos")
    void deveExecutarValidacaoDeCamposEspecificos() {
        final PessoaFake pessoa = new PessoaFake(
                PessoaID.unique(),
                null,
                null
        );

        final PessoaValidatorFake validator =
                new PessoaValidatorFake(
                        pessoa,
                        new ThrowsValidationHandler()
                );

        validator.validate();

        assertTrue(
                validator.foiExecutadaValidacaoEspecifica()
        );
    }

    @Test
    @DisplayName("Deve validar corretamente quando email estiver preenchido")
    void deveValidarComEmailPreenchido() {
        final PessoaFake pessoa = new PessoaFake(
                PessoaID.unique(),
                Email.from("teste@email.com"),
                null
        );

        final PessoaValidatorFake validator =
                new PessoaValidatorFake(
                        pessoa,
                        new ThrowsValidationHandler()
                );

        assertDoesNotThrow(validator::validate);

        assertTrue(
                validator.foiExecutadaValidacaoEspecifica()
        );
    }

    @Test
    @DisplayName("Deve validar corretamente quando telefone estiver preenchido")
    void deveValidarComTelefonePreenchido() {
        final PessoaFake pessoa = new PessoaFake(
                PessoaID.unique(),
                null,
                Telefone.from("11999999999")
        );

        final PessoaValidatorFake validator =
                new PessoaValidatorFake(
                        pessoa,
                        new ThrowsValidationHandler()
                );

        assertDoesNotThrow(validator::validate);

        assertTrue(
                validator.foiExecutadaValidacaoEspecifica()
        );
    }

    @Test
    @DisplayName("Deve validar corretamente quando email e telefone estiverem preenchidos")
    void deveValidarComEmailETelefonePreenchidos() {
        final PessoaFake pessoa = new PessoaFake(
                PessoaID.unique(),
                Email.from("teste@email.com"),
                Telefone.from("11999999999")
        );

        final PessoaValidatorFake validator =
                new PessoaValidatorFake(
                        pessoa,
                        new ThrowsValidationHandler()
                );

        assertDoesNotThrow(validator::validate);

        assertTrue(
                validator.foiExecutadaValidacaoEspecifica()
        );
    }
}
