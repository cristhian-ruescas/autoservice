package com.autoservice.domain.part;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "part", schema = "catalog")
public class Part extends AggregateRoot<PartID> {

    @EmbeddedId
    private PartID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "stock_quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    protected Part() {
        super();
    }

    private Part(final PartID anId, final String aName, final Integer aQuantity, final BigDecimal aUnitPrice) {
        super(anId);
        this.id = anId;
        this.name = aName;
        this.quantity = aQuantity;
        this.unitPrice = aUnitPrice;
    }

    public static Part newPart(final String aName, final Integer aQuantity, final BigDecimal aUnitPrice) {
        return new Part(PartID.unique(), aName, aQuantity, aUnitPrice);
    }

    public static Part withId(final PartID anId, final String aName, final Integer aQuantity, final BigDecimal aUnitPrice) {
        return new Part(anId, aName, aQuantity, aUnitPrice);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        if (name == null || name.isBlank()) {
            handler.append(new Error("Name should not be null or empty"));
        }
        if (quantity == null || quantity < 0) {
            handler.append(new Error("Quantity should be greater than or equal to zero"));
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            handler.append(new Error("Unit price should be greater than or equal to zero"));
        }
    }

    @Override
    public PartID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}

