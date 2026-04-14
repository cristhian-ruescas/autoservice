package com.autoservice.domain.workorder;

import com.autoservice.domain.service.WorkshopService;
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
@Table(name = "work_order_service_item", schema = "workorder")
public class WorkOrderServiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private WorkshopService service;

    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    protected WorkOrderServiceItem() {
    }

    private WorkOrderServiceItem(final WorkOrder aWorkOrder, final WorkshopService aService, final BigDecimal aPrice) {
        this.workOrder = aWorkOrder;
        this.service = aService;
        this.price = aPrice;
    }

    public static WorkOrderServiceItem of(final WorkOrder aWorkOrder, final WorkshopService aService) {
        return new WorkOrderServiceItem(aWorkOrder, aService, aService.getBasePrice());
    }

    public WorkshopService getService() {
        return service;
    }

    public BigDecimal getPrice() {
        return price;
    }
}

