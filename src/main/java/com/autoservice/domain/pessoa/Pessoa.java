package com.autoservice.domain.pessoa;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "pessoa", schema = "cadastro")
public abstract class Pessoa extends AggregateRoot<PessoaID> {

    @EmbeddedId
    private PessoaID embeddedId;
    @Embedded
    private Email email;
    @Embedded
    private Telefone telefone;

    protected Pessoa() {
        super();
    }

    protected Pessoa(PessoaID id, Email email, Telefone telefone) {
        super(id);
        this.embeddedId = id;
        this.email = email;
        this.telefone = telefone;
    }

    @Override
    public PessoaID getId() {
        return embeddedId;
    }

    public Email getEmail() {
        return email;
    }

    public Telefone getTelefone() {
        return telefone;
    }
}
