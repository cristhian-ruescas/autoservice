package com.autoservice.infrastructure.persistence.entity;

import com.autoservice.domain.ordemcompra.ItemOrdemCompraID;
import com.autoservice.domain.ordemcompra.OrdemCompraID;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.infrastructure.persistence.converter.ItemOrdemCompraIdConverter;
import com.autoservice.infrastructure.persistence.converter.OrdemCompraIdConverter;
import com.autoservice.infrastructure.persistence.converter.PecaIdConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_ordem_compra", schema = "estoque")
public class ItemOrdemCompraJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @Convert(converter = ItemOrdemCompraIdConverter.class)
    private ItemOrdemCompraID id;

    @Column(name = "ordem_compra_id", nullable = false)
    @Convert(converter = OrdemCompraIdConverter.class)
    private OrdemCompraID ordemCompraId;

    @Column(name = "peca_id", nullable = false)
    @Convert(converter = PecaIdConverter.class)
    private PecaID pecaId;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    public ItemOrdemCompraJpaEntity() {
    }

    public ItemOrdemCompraID getId() { return id; }
    public void setId(ItemOrdemCompraID id) { this.id = id; }
    public OrdemCompraID getOrdemCompraId() { return ordemCompraId; }
    public void setOrdemCompraId(OrdemCompraID ordemCompraId) { this.ordemCompraId = ordemCompraId; }
    public PecaID getPecaId() { return pecaId; }
    public void setPecaId(PecaID pecaId) { this.pecaId = pecaId; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
