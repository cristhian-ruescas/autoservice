package com.autoservice.domain.customer;

import com.autoservice.domain.Identifier;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class CustomerID extends Identifier {

    @Column(name = "id", nullable = false)
    private final String value;

    protected CustomerID() {
        this.value = null;
    }

    private CustomerID(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static CustomerID unique() {
        return new CustomerID(UUID.randomUUID().toString().toLowerCase());
    }

    public static CustomerID from(final String anId) {
        return new CustomerID(anId);
    }

    public static CustomerID from(final UUID anId) {
        return new CustomerID(anId.toString().toLowerCase());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final CustomerID that = (CustomerID) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
