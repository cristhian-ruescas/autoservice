package com.autoservice.domain.workorder;

import com.autoservice.domain.part.Part;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "work_order_part_item", schema = "workorder")
public class WorkOrderPartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    protected WorkOrderPartItem() {
    }

    private WorkOrderPartItem(final WorkOrder aWorkOrder, final Part aPart, final Integer aQuantity, final BigDecimal anUnitPrice) {
        this.workOrder = aWorkOrder;
        this.part = aPart;
        this.quantity = aQuantity;
        this.unitPrice = anUnitPrice;
    }

    public static WorkOrderPartItem of(final WorkOrder aWorkOrder, final Part aPart, final Integer aQuantity) {
        return new WorkOrderPartItem(aWorkOrder, aPart, aQuantity, aPart.getUnitPrice());
    }

    public Part getPart() {
        return part;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}

