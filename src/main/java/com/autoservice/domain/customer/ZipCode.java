package com.autoservice.domain.customer;

import com.autoservice.domain.ValueObject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class ZipCode extends ValueObject {

    @Column(name = "zip_code")
    private final String value;

    protected ZipCode() {
        this.value = null;
    }

    private ZipCode(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static ZipCode from(final String aZipCode) {
        return new ZipCode(aZipCode);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final ZipCode that = (ZipCode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
