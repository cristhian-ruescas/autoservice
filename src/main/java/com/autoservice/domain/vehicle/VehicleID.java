package com.autoservice.domain.vehicle;

import com.autoservice.domain.Identifier;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class VehicleID extends Identifier {

    @Column(name = "id", nullable = false)
    private final String value;

    protected VehicleID() {
        this.value = null;
    }

    private VehicleID(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static VehicleID unique() {
        return new VehicleID(UUID.randomUUID().toString().toLowerCase());
    }

    public static VehicleID from(final String anId) {
        return new VehicleID(anId);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final VehicleID that = (VehicleID) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}

