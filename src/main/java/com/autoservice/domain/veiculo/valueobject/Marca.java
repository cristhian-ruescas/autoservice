package com.autoservice.domain.veiculo.valueobject;

import com.autoservice.domain.ValueObject;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.veiculo.validators.MarcaValidator;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Marca extends ValueObject {

    @Column(
            name = "marca",
            nullable = false
    )
    private final String value;

    protected Marca() {
        this.value = null;
    }

    private Marca(final String value) {
        this.value = value;
    }

    public static Marca from(final String value) {
        Marca newMarca = new Marca(value);
        final NotificationValidationHandler handler =
                new NotificationValidationHandler();
        newMarca.validate(handler);
        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
        return newMarca;
    }

    public void validate(ValidationHandler handler) {
        new MarcaValidator(this, handler).validate();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Marca marca = (Marca) o;
        return Objects.equals(value, marca.value);
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
