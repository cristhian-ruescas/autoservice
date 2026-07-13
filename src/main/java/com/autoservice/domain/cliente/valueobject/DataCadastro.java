package com.autoservice.domain.cliente.valueobject;

import com.autoservice.domain.ValueObject;
import com.autoservice.domain.cliente.validators.DataCadastroValidator;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DataCadastro extends ValueObject {

    private final LocalDate value;

    protected DataCadastro() {
        this.value = null;
    }

    private DataCadastro(final LocalDate value) {
        Objects.requireNonNull(value, "Data não pode ser nula");
        this.value = value;
    }

    public static DataCadastro from(final LocalDate value) {
        final DataCadastro newDataCadastro = new DataCadastro(value);
        final NotificationValidationHandler handler =
                new NotificationValidationHandler();
        newDataCadastro.validate(handler);
        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
        return newDataCadastro;
    }

    public void validate(ValidationHandler handler) {
        new DataCadastroValidator(this, handler).validate();
    }

    public LocalDate getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DataCadastro that = (DataCadastro) o;
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
