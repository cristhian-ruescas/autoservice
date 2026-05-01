package com.autoservice.domain.workorder;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.domain.customer.Customer;
import com.autoservice.domain.part.Part;
import com.autoservice.domain.service.WorkshopService;
import com.autoservice.domain.vehicle.Vehicle;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "work_order", schema = "workorder")
public class WorkOrder extends AggregateRoot<WorkOrderID> {

    @EmbeddedId
    private WorkOrderID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WorkOrderStatus status;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkOrderServiceItem> services = new ArrayList<>();

    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkOrderPartItem> parts = new ArrayList<>();

    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    protected WorkOrder() {
        super();
    }

    private WorkOrder(final WorkOrderID anId, final Customer aCustomer, final Vehicle aVehicle) {
        super(anId);
        this.id = anId;
        this.customer = aCustomer;
        this.vehicle = aVehicle;
        this.status = WorkOrderStatus.RECEIVED;
        this.totalAmount = BigDecimal.ZERO;
        this.startDateTime = LocalDateTime.now();
    }

    public static WorkOrder newWorkOrder(final Customer aCustomer, final Vehicle aVehicle) {
        return new WorkOrder(WorkOrderID.unique(), aCustomer, aVehicle);
    }

    public static WorkOrder withId(final WorkOrderID anId, final Customer aCustomer, final Vehicle aVehicle) {
        return new WorkOrder(anId, aCustomer, aVehicle);
    }

    public void replaceServices(final List<WorkshopService> selectedServices) {
        this.services.clear();
        selectedServices.forEach(service -> this.services.add(WorkOrderServiceItem.of(this, service)));
        recalculateTotal();
    }

    public void replaceParts(final List<PartSelection> selectedParts) {
        this.parts.clear();
        selectedParts.forEach(item -> this.parts.add(WorkOrderPartItem.of(this, item.part(), item.quantity())));
        recalculateTotal();
    }

    public void changeStatus(final WorkOrderStatus aStatus) {
        this.status = aStatus;
        if (aStatus == WorkOrderStatus.COMPLETED) {
            this.endDateTime = LocalDateTime.now();
        }
    }

    public void reassign(final Customer aCustomer, final Vehicle aVehicle) {
        this.customer = aCustomer;
        this.vehicle = aVehicle;
    }

    public void recalculateTotal() {
        BigDecimal servicesTotal = services.stream()
                .map(WorkOrderServiceItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal partsTotal = parts.stream()
                .map(WorkOrderPartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalAmount = servicesTotal.add(partsTotal);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        if (customer == null) {
            handler.append(new Error("Customer is required"));
        }
        if (vehicle == null) {
            handler.append(new Error("Vehicle is required"));
        }
        if (status == null) {
            handler.append(new Error("Status is required"));
        }
        if (services.isEmpty()) {
            handler.append(new Error("Work order should contain at least one service"));
        }
    }

    @Override
    public WorkOrderID getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public WorkOrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<WorkOrderServiceItem> getServices() {
        return List.copyOf(services);
    }

    public List<WorkOrderPartItem> getParts() {
        return List.copyOf(parts);
    }

    public record PartSelection(Part part, Integer quantity) {
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }
}


