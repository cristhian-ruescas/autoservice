package com.autoservice.domain.pessoa.validators;

import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;

public class PessoaFisicaValidator extends PessoaValidator {

    public PessoaFisicaValidator(final PessoaFisica pessoa, final ValidationHandler aHandler) {
        super(pessoa, aHandler);
    }

    @Override
    protected void validateSpecificFields() {
        PessoaFisica pf = (PessoaFisica) this.pessoa;
        validateNome(pf);
        validateCpf(pf);
    }

    public void validateNome(PessoaFisica pessoa) {
        final var nome = pessoa.getNome();
        if (nome == null || nome.isBlank()) {
            this.validationHandler().append(new Error("Nome não pode ser vazio"));
            return;
        }

        int length = nome.trim().length();
        if (length > 255 || length < 3) {
            this.validationHandler().append(new Error("Nome deve possuir entre 3 e 255 caracteres"));
        }
    }

    public void validateCpf(PessoaFisica pessoa) {
        if (pessoa.getCpf() != null) {
            new CPFValidator(pessoa.getCpf(), this.validationHandler()).validate();
        }
    }
}