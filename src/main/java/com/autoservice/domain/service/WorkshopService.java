package com.autoservice.domain.service;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "service_catalog", schema = "catalog")
public class WorkshopService extends AggregateRoot<WorkshopServiceID> {

    @EmbeddedId
    private WorkshopServiceID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "base_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal basePrice;

    protected WorkshopService() {
        super();
    }

    private WorkshopService(final WorkshopServiceID anId, final String aName, final String aDescription, final BigDecimal aBasePrice) {
        super(anId);
        this.id = anId;
        this.name = aName;
        this.description = aDescription;
        this.basePrice = aBasePrice;
    }

    public static WorkshopService newService(final String aName, final String aDescription, final BigDecimal aBasePrice) {
        return new WorkshopService(WorkshopServiceID.unique(), aName, aDescription, aBasePrice);
    }

    public static WorkshopService withId(final WorkshopServiceID anId, final String aName, final String aDescription, final BigDecimal aBasePrice) {
        return new WorkshopService(anId, aName, aDescription, aBasePrice);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        if (name == null || name.isBlank()) {
            handler.append(new Error("Name should not be null or empty"));
        }
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) < 0) {
            handler.append(new Error("Base price should be greater than or equal to zero"));
        }
    }

    @Override
    public WorkshopServiceID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }
}

