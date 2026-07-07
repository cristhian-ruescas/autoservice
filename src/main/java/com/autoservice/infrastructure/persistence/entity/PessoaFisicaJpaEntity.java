package com.autoservice.infrastructure.persistence.entity;

import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.infrastructure.persistence.converter.CPFConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "pessoa_fisica", schema = "cadastro")
public class PessoaFisicaJpaEntity extends PessoaJpaEntity {

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "cpf", nullable = false, unique = true)
    @Convert(converter = CPFConverter.class)
    private CPF cpf;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public CPF getCpf() { return cpf; }
    public void setCpf(CPF cpf) { this.cpf = cpf; }
}
