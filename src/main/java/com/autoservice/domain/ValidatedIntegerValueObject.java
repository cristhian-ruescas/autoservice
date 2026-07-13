package com.autoservice.domain;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;

import java.util.Objects;
import java.util.function.Function;

public abstract class ValidatedIntegerValueObject extends ValueObject {

    private final Integer value;

    protected ValidatedIntegerValueObject() {
        this.value = null;
    }

    protected ValidatedIntegerValueObject(final Integer rawValue) {
        this.value = rawValue;
    }

    protected static <T extends ValidatedIntegerValueObject> T validated(
            final Integer rawValue,
            final Function<Integer, T> factory
    ) {
        final T instance = factory.apply(rawValue);
        final NotificationValidationHandler handler = new NotificationValidationHandler();
        instance.validate(handler);

        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }

        return instance;
    }

    public Integer getValue() {
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
        final ValidatedIntegerValueObject that = (ValidatedIntegerValueObject) other;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
