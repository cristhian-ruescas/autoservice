package com.autoservice.domain.ordemcompra.valueobject;

import com.autoservice.domain.ValueObject;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemcompra.validators.DataCompraValidator;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Embeddable
public class DataCompra extends ValueObject {

    @Column(name = "data_compra", nullable = false)
    private final LocalDate value;

    protected DataCompra() {
        this.value = null;
    }

    private DataCompra(final LocalDate value) {
        this.value = Objects.requireNonNull(value, "Data da compra não pode ser nula");
    }

    public static DataCompra from(final LocalDate value) {
        final DataCompra dataCompra = new DataCompra(value);
        final NotificationValidationHandler handler = new NotificationValidationHandler();
        dataCompra.validate(handler);
        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
        return dataCompra;
    }

    public void validate(final ValidationHandler handler) {
        new DataCompraValidator(this, handler).validate();
    }

    public LocalDate getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DataCompra that = (DataCompra) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        if (value == null) return "null";
        return value.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
