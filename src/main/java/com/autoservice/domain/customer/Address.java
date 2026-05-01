package com.autoservice.domain.customer;

import com.autoservice.domain.ValueObject;
import jakarta.persistence.*;

import java.util.Objects;

@Embeddable
public class Address extends ValueObject {

    @Column(name = "street")
    private final String street;

    @Column(name = "number")
    private final String number;

    @Column(name = "complement")
    private final String complement;

    @Column(name = "neighborhood")
    private final String neighborhood;

    @Column(name = "city")
    private final String city;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private final State state;

    @Embedded
    private final ZipCode zipCode;

    protected Address() {
        this.street = null;
        this.number = null;
        this.complement = null;
        this.neighborhood = null;
        this.city = null;
        this.state = null;
        this.zipCode = null;
    }

    private Address(String street, String number, String complement, String neighborhood, String city, State state, ZipCode zipCode) {
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public static Address from(String street, String number, String complement, String neighborhood, String city, State state, ZipCode zipCode) {
        return new Address(street, number, complement, neighborhood, city, state, zipCode);
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getComplement() {
        return complement;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getCity() {
        return city;
    }

    public State getState() {
        return state;
    }

    public ZipCode getZipCode() {
        return zipCode;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final Address that = (Address) o;
        return Objects.equals(street, that.street) &&
                Objects.equals(number, that.number) &&
                Objects.equals(complement, that.complement) &&
                Objects.equals(neighborhood, that.neighborhood) &&
                Objects.equals(city, that.city) &&
                Objects.equals(state, that.state) &&
                Objects.equals(zipCode, that.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, number, complement, neighborhood, city, state, zipCode);
    }
}
