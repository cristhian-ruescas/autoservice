package com.autoservice.domain.veiculo.valueobject;

import com.autoservice.domain.ValueObject;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.veiculo.validators.CorValidator;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Cor extends ValueObject {

    @Column(
            name = "cor",
            nullable = false
    )
    private final String value;

    protected Cor() {
        this.value = null;
    }

    private Cor(final String value) {
        this.value = value;
    }

    public static Cor from(final String value) {
        Cor newCor = new Cor(value);
        NotificationValidationHandler handler = new NotificationValidationHandler();
        newCor.validate(handler);
        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
        return newCor;
    }

    public void validate(final ValidationHandler handler) {
        new CorValidator(this, handler).validate();
    }


    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cor cor = (Cor) o;
        return Objects.equals(value, cor.value);
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
