package com.autoservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "peca", schema = "estoque")
public class PecaJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "descricao", nullable = false, length = 180)
    private String descricao;

    @Column(name = "codigo", nullable = false, unique = true, length = 80)
    private String codigo;

    @Column(name = "marca", nullable = false, length = 120)
    private String marca;

    @Column(name = "valor_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "estoque_id")
    private String estoqueId;

    @Column(name = "tipo_veiculo_id")
    private String tipoVeiculoId;

    public PecaJpaEntity() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }
    public String getEstoqueId() { return estoqueId; }
    public void setEstoqueId(String estoqueId) { this.estoqueId = estoqueId; }
    public String getTipoVeiculoId() { return tipoVeiculoId; }
    public void setTipoVeiculoId(String tipoVeiculoId) { this.tipoVeiculoId = tipoVeiculoId; }
}
