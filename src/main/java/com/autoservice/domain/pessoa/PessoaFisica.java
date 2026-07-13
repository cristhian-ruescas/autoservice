package com.autoservice.domain.pessoa;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.validators.PessoaFisicaValidator;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;

public class PessoaFisica extends Pessoa {

    private String nome;

    private CPF cpf;

    protected PessoaFisica() {
        super();
    }

    private PessoaFisica(PessoaID id, Email email, Telefone telefone, String nome, CPF cpf) {
        super(id, email, telefone);
        this.nome = nome;
        this.cpf = cpf;
    }

    public static PessoaFisica newPessoaFisica(Email email, Telefone telefone, String nome, CPF cpf) {
        PessoaFisica newPessoaFisica =
                new PessoaFisica(PessoaID.unique(), email, telefone, nome, cpf);
        final NotificationValidationHandler handler =
                new NotificationValidationHandler();
        newPessoaFisica.validate(handler);
        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
        return newPessoaFisica;
    }

    public static PessoaFisica withId(PessoaID id, Email email, Telefone telefone, String nome, CPF cpf) {
        return new PessoaFisica(id, email, telefone, nome, cpf);
    }

    @Override
    public void validate(ValidationHandler handler) {
        new PessoaFisicaValidator(this, handler).validate();
    }

    public String getNome() {
        return nome;
    }

    public CPF getCpf() {
        return cpf;
    }
}
