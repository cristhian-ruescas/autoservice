package com.autoservice.domain.part;

import com.autoservice.domain.Identifier;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class PartID extends Identifier {

    @Column(name = "id", nullable = false)
    private final String value;

    protected PartID() {
        this.value = null;
    }

    private PartID(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static PartID unique() {
        return new PartID(UUID.randomUUID().toString().toLowerCase());
    }

    public static PartID from(final String anId) {
        return new PartID(anId);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final PartID partID = (PartID) o;
        return Objects.equals(value, partID.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}

