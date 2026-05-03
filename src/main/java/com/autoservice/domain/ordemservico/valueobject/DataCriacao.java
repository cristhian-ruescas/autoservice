package com.autoservice.domain.ordemservico.valueobject;

import com.autoservice.domain.ValueObject;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.validators.DataCriacaoValidator;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Embeddable
public class DataCriacao extends ValueObject {

    @Column(
            name = "data_criacao",
            nullable = false
    )
    private final LocalDate value;

    protected DataCriacao() {
        this.value = null;
    }

    private DataCriacao(final LocalDate value) {
        Objects.requireNonNull(value, "Data de criação não pode ser nula");
        this.value = value;
    }

    public static DataCriacao from(final LocalDate value) {
        final DataCriacao newData = new DataCriacao(value);
        final NotificationValidationHandler handler = new NotificationValidationHandler();
        newData.validate(handler);
        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
        return newData;
    }

    public void validate(final ValidationHandler handler) {
        new DataCriacaoValidator(this, handler).validate();
    }

    public LocalDate getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DataCriacao that = (DataCriacao) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        if (value == null) return "null";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return value.format(formatter);
    }
}
