package com.autoservice.domain.pessoa;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;

public abstract class Pessoa extends AggregateRoot<PessoaID> {

    private PessoaID id;
    private Email email;
    private Telefone telefone;

    protected Pessoa() {
        super();
    }

    protected Pessoa(PessoaID id, Email email, Telefone telefone) {
        super(id);
        this.id = id;
        this.email = email;
        this.telefone = telefone;
    }

    @Override
    public PessoaID getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public Telefone getTelefone() {
        return telefone;
    }
}
