package com.autoservice.infrastructure.persistence.entity;

import com.autoservice.domain.servico.ServicoID;
import com.autoservice.infrastructure.persistence.converter.ServicoIdConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "servico_cadastro", schema = "cadastro")
public class ServicoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @Convert(converter = ServicoIdConverter.class)
    private ServicoID id;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "valor_referencia", precision = 10, scale = 2)
    private BigDecimal valorReferencia;

    public ServicoJpaEntity() {
    }

    public ServicoID getId() { return id; }
    public void setId(ServicoID id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getValorReferencia() { return valorReferencia; }
    public void setValorReferencia(BigDecimal valorReferencia) { this.valorReferencia = valorReferencia; }
}
