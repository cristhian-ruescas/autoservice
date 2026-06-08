package com.autoservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_ordem_compra", schema = "estoque")
public class ItemOrdemCompraJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "ordem_compra_id", nullable = false)
    private String ordemCompraId;

    @Column(name = "peca_id", nullable = false)
    private String pecaId;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    public ItemOrdemCompraJpaEntity() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrdemCompraId() { return ordemCompraId; }
    public void setOrdemCompraId(String ordemCompraId) { this.ordemCompraId = ordemCompraId; }
    public String getPecaId() { return pecaId; }
    public void setPecaId(String pecaId) { this.pecaId = pecaId; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
