package com.autoservice.infrastructure.persistence.entity;

import com.autoservice.domain.estoque.EstoqueID;
import com.autoservice.infrastructure.persistence.converter.EstoqueIdConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estoque", schema = "estoque")
public class EstoqueJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @Convert(converter = EstoqueIdConverter.class)
    private EstoqueID id;

    @Column(name = "quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel;

    @Column(name = "quantidade_minima", nullable = false)
    private Integer quantidadeMinima;

    @Column(name = "localizacao", length = 120)
    private String localizacao;

    public EstoqueJpaEntity() {
    }

    public EstoqueID getId() { return id; }
    public void setId(EstoqueID id) { this.id = id; }
    public Integer getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) { this.quantidadeDisponivel = quantidadeDisponivel; }
    public Integer getQuantidadeMinima() { return quantidadeMinima; }
    public void setQuantidadeMinima(Integer quantidadeMinima) { this.quantidadeMinima = quantidadeMinima; }
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
}
