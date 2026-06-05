package com.autoservice.infrastructure.persistence.entity;

import com.autoservice.domain.ordemcompra.OrdemCompraID;
import com.autoservice.domain.ordemcompra.enums.OrdemCompraStatus;
import com.autoservice.domain.ordemcompra.valueobject.DataCompra;
import com.autoservice.infrastructure.persistence.converter.DataCompraConverter;
import com.autoservice.infrastructure.persistence.converter.OrdemCompraIdConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ordem_compra", schema = "estoque")
public class OrdemCompraJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @Convert(converter = OrdemCompraIdConverter.class)
    private OrdemCompraID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrdemCompraStatus status;

    @Column(name = "data_compra", nullable = false)
    @Convert(converter = DataCompraConverter.class)
    private DataCompra dataCompra;

    public OrdemCompraJpaEntity() {
    }

    public OrdemCompraID getId() { return id; }
    public void setId(OrdemCompraID id) { this.id = id; }
    public OrdemCompraStatus getStatus() { return status; }
    public void setStatus(OrdemCompraStatus status) { this.status = status; }
    public DataCompra getDataCompra() { return dataCompra; }
    public void setDataCompra(DataCompra dataCompra) { this.dataCompra = dataCompra; }
}
