package com.autoservice.infrastructure.persistence.converter;

import jakarta.persistence.AttributeConverter;

import java.util.function.Function;

public abstract class ValueObjectStringConverter<VALUE> implements AttributeConverter<VALUE, String> {

    private final Function<String, VALUE> fromString;
    private final Function<VALUE, String> toStringValue;

    protected ValueObjectStringConverter(
            final Function<String, VALUE> fromString,
            final Function<VALUE, String> toStringValue
    ) {
        this.fromString = fromString;
        this.toStringValue = toStringValue;
    }

    @Override
    public String convertToDatabaseColumn(final VALUE attribute) {
        return attribute == null ? null : this.toStringValue.apply(attribute);
    }

    @Override
    public VALUE convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : this.fromString.apply(dbData);
    }
}
