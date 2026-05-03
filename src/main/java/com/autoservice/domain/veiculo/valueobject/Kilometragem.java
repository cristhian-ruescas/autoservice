package com.autoservice.domain.veiculo.valueobject;

import com.autoservice.domain.ValueObject;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.veiculo.validators.KilometragemValidator;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Kilometragem extends ValueObject {

    @Column(
            name = "kilometragem",
            nullable = false
    )
    private final Integer value;

    protected Kilometragem() {
        this.value = null;
    }

    private Kilometragem(final Integer value) {
        this.value = value;
    }

    public static Kilometragem from(final Integer value) {
        Kilometragem newKilometragem = new Kilometragem(value);
        NotificationValidationHandler handler = new NotificationValidationHandler();
        newKilometragem.validate(handler);
        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
        return newKilometragem;
    }

    public void validate(final ValidationHandler handler) {
        new KilometragemValidator(this, handler).validate();
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kilometragem that = (Kilometragem) o;
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
