package com.autoservice.infrastructure.persistence.entity;

import com.autoservice.domain.cliente.valueobject.DataCadastro;
import com.autoservice.infrastructure.persistence.converter.DataCadastroConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente", schema = "cadastro")
public class ClienteJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "pessoa_id", nullable = false)
    private String pessoaId;

    @Column(name = "data_cadastro", nullable = false)
    @Convert(converter = DataCadastroConverter.class)
    private DataCadastro dataCadastro;

    public ClienteJpaEntity() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPessoaId() { return pessoaId; }
    public void setPessoaId(String pessoaId) { this.pessoaId = pessoaId; }
    public DataCadastro getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(DataCadastro dataCadastro) { this.dataCadastro = dataCadastro; }
}
