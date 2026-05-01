package com.autoservice.domain.customer;

import com.autoservice.domain.ValueObject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class PhoneNumber extends ValueObject {

    @Column(name = "phone_number")
    private final String value;

    protected PhoneNumber() {
        this.value = null;
    }

    private PhoneNumber(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static PhoneNumber from(final String aPhoneNumber) {
        return new PhoneNumber(aPhoneNumber);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final PhoneNumber that = (PhoneNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
