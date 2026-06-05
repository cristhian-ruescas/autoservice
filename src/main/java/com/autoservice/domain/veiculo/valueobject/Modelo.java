package com.autoservice.domain.veiculo.valueobject;

import com.autoservice.domain.ValueObject;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.veiculo.validators.ModeloValidator;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;

import java.util.Objects;

public class Modelo extends ValueObject {

    private final String value;

    protected Modelo() {
        this.value = null;
    }

    private Modelo(final String value) {
        this.value = value;
    }

    public static Modelo from(final String value) {
        Modelo newModelo = new Modelo(value);
        NotificationValidationHandler handler = new NotificationValidationHandler();
        newModelo.validate(handler);
        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
        return newModelo;
    }

    public void validate(final ValidationHandler handler) {
        new ModeloValidator(this, handler).validate();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Modelo modelo = (Modelo) o;
        return Objects.equals(value, modelo.value);
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
