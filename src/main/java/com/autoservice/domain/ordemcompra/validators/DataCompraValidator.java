package com.autoservice.domain.ordemcompra.validators;

import com.autoservice.domain.ordemcompra.valueobject.DataCompra;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

import java.time.LocalDate;

public class DataCompraValidator extends Validator {

    private final DataCompra dataCompra;

    public DataCompraValidator(final DataCompra dataCompra, final ValidationHandler handler) {
        super(handler);
        this.dataCompra = dataCompra;
    }

    @Override
    public void validate() {
        final LocalDate value = this.dataCompra.getValue();

        if (value == null) {
            this.validationHandler().append(new Error("Data da compra não deve ser nula"));
            return;
        }

        if (value.isAfter(LocalDate.now())) {
            this.validationHandler().append(new Error("Data da compra não pode ser no futuro"));
        }
    }
}
