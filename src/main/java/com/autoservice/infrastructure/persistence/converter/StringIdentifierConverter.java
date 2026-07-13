package com.autoservice.infrastructure.persistence.converter;

import jakarta.persistence.AttributeConverter;

import java.util.function.Function;

public abstract class StringIdentifierConverter<ID> implements AttributeConverter<ID, String> {

    private final Function<String, ID> fromString;
    private final Function<ID, String> toStringValue;

    protected StringIdentifierConverter(
            final Function<String, ID> fromString,
            final Function<ID, String> toStringValue
    ) {
        this.fromString = fromString;
        this.toStringValue = toStringValue;
    }

    @Override
    public String convertToDatabaseColumn(final ID attribute) {
        return attribute == null ? null : this.toStringValue.apply(attribute);
    }

    @Override
    public ID convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : this.fromString.apply(dbData);
    }
}
