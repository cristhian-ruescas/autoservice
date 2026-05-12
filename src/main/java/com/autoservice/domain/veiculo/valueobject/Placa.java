package com.autoservice.domain.veiculo.valueobject;

import com.autoservice.domain.ValueObject;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.veiculo.validators.PlacaValidator;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Placa extends ValueObject {

    @Column(
            name = "placa",
            nullable = false
    )
    private final String value;

    protected Placa() {
        this.value = null;
    }


    private Placa(final String value) {
        this.value = value == null ? null : value.trim().toUpperCase();
    }


    public static Placa from(final String valor) {
        Placa newPlaca = new Placa(valor);
        NotificationValidationHandler handler = new NotificationValidationHandler();
        newPlaca.validate(handler);
        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
        return newPlaca;
    }

    public void validate(final ValidationHandler handler) {
        new PlacaValidator(this, handler).validate();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Placa placa = (Placa) o;
        return Objects.equals(value, placa.value);
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
