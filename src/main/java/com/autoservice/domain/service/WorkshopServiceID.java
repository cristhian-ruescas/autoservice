package com.autoservice.domain.service;

import com.autoservice.domain.Identifier;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class WorkshopServiceID extends Identifier {

    @Column(name = "id", nullable = false)
    private final String value;

    protected WorkshopServiceID() {
        this.value = null;
    }

    private WorkshopServiceID(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static WorkshopServiceID unique() {
        return new WorkshopServiceID(UUID.randomUUID().toString().toLowerCase());
    }

    public static WorkshopServiceID from(final String anId) {
        return new WorkshopServiceID(anId);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final WorkshopServiceID that = (WorkshopServiceID) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}

