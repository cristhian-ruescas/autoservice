package com.autoservice.domain.cliente.validators;

import com.autoservice.domain.cliente.Cliente;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

public class ClienteValidator extends Validator {

    protected final Cliente cliente;

    public ClienteValidator(final Cliente cliente, ValidationHandler handler) {
        super(handler);
        this.cliente = cliente;
    }

    @Override
    public void validate() {
        checkPessoaId();
    }

    private void checkPessoaId() {
        if (this.cliente.getPessoaId() == null) {
            this.validationHandler().append(new Error("'pessoaId' não deve ser nulo"));
        }
    }
}
