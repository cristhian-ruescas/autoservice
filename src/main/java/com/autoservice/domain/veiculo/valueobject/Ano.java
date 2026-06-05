package com.autoservice.domain.veiculo.valueobject;

import com.autoservice.domain.ValueObject;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.veiculo.validators.AnoValidator;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;

import java.util.Objects;

public class Ano extends ValueObject {

    private final Integer value;

    protected Ano() {
        this.value = null;
    }

    private Ano(final Integer value) {
        this.value = value;
    }

    public static Ano from(final Integer ano) {
        Ano newAno = new Ano(ano);
        NotificationValidationHandler handler = new NotificationValidationHandler();
        newAno.validate(handler);
        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
        return newAno;
    }

    public void validate(ValidationHandler handler) {
        new AnoValidator(this, handler).validate();
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Ano ano = (Ano) o;
        return Objects.equals(value, ano.value);
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
