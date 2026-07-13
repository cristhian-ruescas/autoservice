package com.autoservice.infrastructure.persistence.entity;

import com.autoservice.domain.itemservico.enums.ItemServicoTipo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "item_servico", schema = "servico")
public class ItemServicoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "ordem_servico_id", nullable = false)
    private String ordemServicoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private ItemServicoTipo tipo;

    @Column(name = "descricao", nullable = false, length = 180)
    private String descricao;

    @Column(name = "peca_id")
    private String pecaId;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    public ItemServicoJpaEntity() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrdemServicoId() { return ordemServicoId; }
    public void setOrdemServicoId(String ordemServicoId) { this.ordemServicoId = ordemServicoId; }
    public ItemServicoTipo getTipo() { return tipo; }
    public void setTipo(ItemServicoTipo tipo) { this.tipo = tipo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getPecaId() { return pecaId; }
    public void setPecaId(String pecaId) { this.pecaId = pecaId; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }
}
