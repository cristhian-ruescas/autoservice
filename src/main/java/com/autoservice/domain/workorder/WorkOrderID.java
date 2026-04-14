package com.autoservice.domain.workorder;

import com.autoservice.domain.Identifier;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class WorkOrderID extends Identifier {

    @Column(name = "id", nullable = false)
    private final String value;

    protected WorkOrderID() {
        this.value = null;
    }

    private WorkOrderID(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static WorkOrderID unique() {
        return new WorkOrderID(UUID.randomUUID().toString().toLowerCase());
    }

    public static WorkOrderID from(final String anId) {
        return new WorkOrderID(anId);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final WorkOrderID that = (WorkOrderID) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}

