package com.autoservice.infrastructure.persistence.entity;

import com.autoservice.domain.pessoa.valueobject.CNPJ;
import com.autoservice.infrastructure.persistence.converter.CNPJConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "pessoa_juridica", schema = "cadastro")
public class PessoaJuridicaJpaEntity extends PessoaJpaEntity {

    @Column(name = "razao_social", nullable = false)
    private String razaoSocial;

    @Column(name = "cnpj", nullable = false, unique = true)
    @Convert(converter = CNPJConverter.class)
    private CNPJ cnpj;

    @Column(name = "representante_legal_id")
    private String representanteLegalId;

    public PessoaJuridicaJpaEntity() {
    }

    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }
    public CNPJ getCnpj() { return cnpj; }
    public void setCnpj(CNPJ cnpj) { this.cnpj = cnpj; }
    public String getRepresentanteLegalId() { return representanteLegalId; }
    public void setRepresentanteLegalId(String representanteLegalId) { this.representanteLegalId = representanteLegalId; }
}
