package com.autoservice.domain.vehicle;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.domain.customer.Customer;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import jakarta.persistence.*;

@Entity
@Table(name = "vehicle", schema = "customer")
public class Vehicle extends AggregateRoot<VehicleID> {

    @EmbeddedId
    private VehicleID id;

    @Column(name = "plate", nullable = false)
    private String plate;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "manufacture_year", nullable = false)
    private Integer year;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    protected Vehicle() {
        super();
    }

    private Vehicle(
            final VehicleID anId,
            final String aPlate,
            final String aBrand,
            final String aModel,
            final Integer aYear,
            final Customer aCustomer
    ) {
        super(anId);
        this.id = anId;
        this.plate = aPlate;
        this.brand = aBrand;
        this.model = aModel;
        this.year = aYear;
        this.customer = aCustomer;
    }

    public static Vehicle newVehicle(
            final String aPlate,
            final String aBrand,
            final String aModel,
            final Integer aYear,
            final Customer aCustomer
    ) {
        return new Vehicle(VehicleID.unique(), aPlate, aBrand, aModel, aYear, aCustomer);
    }

    public static Vehicle withId(
            final VehicleID anId,
            final String aPlate,
            final String aBrand,
            final String aModel,
            final Integer aYear,
            final Customer aCustomer
    ) {
        return new Vehicle(anId, aPlate, aBrand, aModel, aYear, aCustomer);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        if (plate == null || plate.isBlank()) {
            handler.append(new Error("Plate should not be null or empty"));
        }
        if (brand == null || brand.isBlank()) {
            handler.append(new Error("Brand should not be null or empty"));
        }
        if (model == null || model.isBlank()) {
            handler.append(new Error("Model should not be null or empty"));
        }
        if (year == null || year < 1900) {
            handler.append(new Error("Year should be greater than or equal to 1900"));
        }
        if (customer == null) {
            handler.append(new Error("Customer is required"));
        }
    }

    @Override
    public VehicleID getId() {
        return id;
    }

    public String getPlate() {
        return plate;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public Integer getYear() {
        return year;
    }

    public Customer getCustomer() {
        return customer;
    }
}

