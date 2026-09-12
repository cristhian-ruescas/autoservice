package com.autoservice.domain.ordemcompra.validators;

import com.autoservice.domain.ordemcompra.OrdemCompra;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

import static java.util.Objects.isNull;

public class OrdemCompraValidator extends Validator {

    private final OrdemCompra ordemCompra;

    public OrdemCompraValidator(final OrdemCompra ordemCompra, final ValidationHandler handler) {
        super(handler);
        this.ordemCompra = ordemCompra;
    }

    @Override
    public void validate() {
        validateStatus();
        validateDataCompra();
    }

    private void validateStatus() {
        if (isNull(this.ordemCompra.getStatus())) {
            this.validationHandler().append(new Error("Status da ordem de compra não deve ser nulo"));
        }
    }

    private void validateDataCompra() {
        if (isNull(this.ordemCompra.getDataCompra())) {
            this.validationHandler().append(new Error("Data da compra não deve ser nula"));
            return;
        }

        this.ordemCompra.getDataCompra().validate(this.validationHandler());
    }
}
