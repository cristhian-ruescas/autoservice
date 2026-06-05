package com.autoservice.infrastructure.persistence.entity;

import com.autoservice.domain.itemservico.ItemServicoID;
import com.autoservice.domain.itemservico.enums.ItemServicoTipo;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.infrastructure.persistence.converter.ItemServicoIdConverter;
import com.autoservice.infrastructure.persistence.converter.OrdemServicoIdConverter;
import com.autoservice.infrastructure.persistence.converter.PecaIdConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
    @Convert(converter = ItemServicoIdConverter.class)
    private ItemServicoID id;

    @Column(name = "ordem_servico_id", nullable = false)
    @Convert(converter = OrdemServicoIdConverter.class)
    private OrdemServicoID ordemServicoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private ItemServicoTipo tipo;

    @Column(name = "descricao", nullable = false, length = 180)
    private String descricao;

    @Column(name = "peca_id")
    @Convert(converter = PecaIdConverter.class)
    private PecaID pecaId;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    public ItemServicoJpaEntity() {
    }

    public ItemServicoID getId() { return id; }
    public void setId(ItemServicoID id) { this.id = id; }
    public OrdemServicoID getOrdemServicoId() { return ordemServicoId; }
    public void setOrdemServicoId(OrdemServicoID ordemServicoId) { this.ordemServicoId = ordemServicoId; }
    public ItemServicoTipo getTipo() { return tipo; }
    public void setTipo(ItemServicoTipo tipo) { this.tipo = tipo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public PecaID getPecaId() { return pecaId; }
    public void setPecaId(PecaID pecaId) { this.pecaId = pecaId; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }
}
