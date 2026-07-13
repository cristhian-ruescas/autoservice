package com.autoservice.domain;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;

import java.util.Objects;
import java.util.function.Function;

public abstract class ValidatedStringValueObject extends ValueObject {

    private final String value;

    protected ValidatedStringValueObject() {
        this.value = null;
    }

    protected ValidatedStringValueObject(final String rawValue) {
        this.value = normalize(rawValue);
    }

    protected String normalize(final String rawValue) {
        return rawValue;
    }

    protected static <T extends ValidatedStringValueObject> T validated(
            final String rawValue,
            final Function<String, T> factory
    ) {
        final T instance = factory.apply(rawValue);
        final NotificationValidationHandler handler = new NotificationValidationHandler();
        instance.validate(handler);

        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }

        return instance;
    }

    public String getValue() {
        return value;
    }

    public abstract void validate(final ValidationHandler handler);

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final ValidatedStringValueObject that = (ValidatedStringValueObject) other;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
