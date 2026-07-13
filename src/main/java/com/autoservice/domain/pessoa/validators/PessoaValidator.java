package com.autoservice.domain.pessoa.validators;

import com.autoservice.domain.pessoa.Pessoa;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

public abstract class PessoaValidator extends Validator {

    protected final Pessoa pessoa;

    protected PessoaValidator(final Pessoa pessoa, ValidationHandler aHandler) {
        super(aHandler);
        this.pessoa = pessoa;
    }

    @Override
    public final void validate() {
        validateCommonFields();
        validateSpecificFields();
    }

    public void validateCommonFields() {
        if (this.pessoa.getEmail() != null) {
            new EmailValidator(this.pessoa.getEmail(), this.validationHandler()).validate();
        }
        if (this.pessoa.getTelefone() != null) {
            new TelefoneValidator(this.pessoa.getTelefone(), this.validationHandler()).validate();
        }
    }

    protected abstract void validateSpecificFields();
}
