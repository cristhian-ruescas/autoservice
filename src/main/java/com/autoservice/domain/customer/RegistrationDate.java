package com.autoservice.domain.customer;

import com.autoservice.domain.ValueObject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class RegistrationDate extends ValueObject {

    @Column(name = "registration_date")
    private final LocalDate value;

    protected RegistrationDate() {
        this.value = null;
    }

    private RegistrationDate(final LocalDate value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static RegistrationDate from(final LocalDate aRegistrationDate) {
        return new RegistrationDate(aRegistrationDate);
    }

    public LocalDate getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final RegistrationDate that = (RegistrationDate) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
