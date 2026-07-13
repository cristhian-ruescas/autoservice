package com.autoservice.domain.pessoa;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.validators.PessoaJuridicaValidator;
import com.autoservice.domain.pessoa.valueobject.CNPJ;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;

public class PessoaJuridica extends Pessoa {

    private String razaoSocial;

    private CNPJ cnpj;

    private PessoaID representanteLegalId;

    protected PessoaJuridica() {
        super();
    }

    private PessoaJuridica(
            PessoaID id,
            Email email,
            Telefone telefone,
            String razaoSocial,
            CNPJ cnpj,
            PessoaID representanteLegalId
    ) {
        super(id, email, telefone);
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.representanteLegalId = representanteLegalId;
    }

    public static PessoaJuridica newPessoaJuridica(Email email, Telefone telefone, String razaoSocial, CNPJ cnpj, PessoaID representanteLegalId) {
        PessoaJuridica newPessoaJuridica =
                new PessoaJuridica(PessoaID.unique(), email, telefone, razaoSocial, cnpj, representanteLegalId);
        final NotificationValidationHandler handler =
                new NotificationValidationHandler();
        newPessoaJuridica.validate(handler);
        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
        return newPessoaJuridica;
    }

    public static PessoaJuridica withId(PessoaID id, Email email, Telefone telefone, String razaoSocial, CNPJ cnpj, PessoaID representanteLegalId) {
        return new PessoaJuridica(id, email, telefone, razaoSocial, cnpj, representanteLegalId);
    }

    @Override
    public void validate(ValidationHandler handler) {
        new PessoaJuridicaValidator(this, handler).validate();
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public CNPJ getCnpj() {
        return cnpj;
    }

    public PessoaID getRepresentanteLegalId() {
        return representanteLegalId;
    }
}
