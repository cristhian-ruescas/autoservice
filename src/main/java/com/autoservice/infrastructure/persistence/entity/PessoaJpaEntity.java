package com.autoservice.infrastructure.persistence.entity;

import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import com.autoservice.infrastructure.persistence.converter.EmailConverter;
import com.autoservice.infrastructure.persistence.converter.TelefoneConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "pessoa", schema = "cadastro")
public abstract class PessoaJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "email", nullable = false, updatable = false)
    @Convert(converter = EmailConverter.class)
    private Email email;

    @Column(name = "telefone", nullable = false)
    @Convert(converter = TelefoneConverter.class)
    private Telefone telefone;

    protected PessoaJpaEntity() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Email getEmail() { return email; }
    public void setEmail(Email email) { this.email = email; }
    public Telefone getTelefone() { return telefone; }
    public void setTelefone(Telefone telefone) { this.telefone = telefone; }
}
