package com.autoservice.domain.customer;

import com.autoservice.domain.ValueObject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Document extends ValueObject {

    @Column(name = "document")
    private final String value;

    protected Document() {
        this.value = null;
    }

    private Document(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static Document from(final String aDocument) {
        return new Document(aDocument);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final Document that = (Document) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
