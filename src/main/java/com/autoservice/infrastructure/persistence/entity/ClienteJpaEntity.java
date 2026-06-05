package com.autoservice.infrastructure.persistence.entity;

import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.domain.cliente.valueobject.DataCadastro;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.infrastructure.persistence.converter.ClienteIdConverter;
import com.autoservice.infrastructure.persistence.converter.DataCadastroConverter;
import com.autoservice.infrastructure.persistence.converter.PessoaIdConverter;
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
    @Convert(converter = ClienteIdConverter.class)
    private ClienteID id;

    @Column(name = "pessoa_id", nullable = false)
    @Convert(converter = PessoaIdConverter.class)
    private PessoaID pessoaId;

    @Column(name = "data_cadastro", nullable = false)
    @Convert(converter = DataCadastroConverter.class)
    private DataCadastro dataCadastro;

    public ClienteJpaEntity() {
    }

    public ClienteID getId() { return id; }
    public void setId(ClienteID id) { this.id = id; }
    public PessoaID getPessoaId() { return pessoaId; }
    public void setPessoaId(PessoaID pessoaId) { this.pessoaId = pessoaId; }
    public DataCadastro getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(DataCadastro dataCadastro) { this.dataCadastro = dataCadastro; }
}
