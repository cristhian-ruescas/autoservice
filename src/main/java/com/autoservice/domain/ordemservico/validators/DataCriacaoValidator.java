package com.autoservice.domain.ordemservico.validators;

import com.autoservice.domain.ordemservico.valueobject.DataCriacao;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

import java.time.LocalDate;

public class DataCriacaoValidator extends Validator {

    private final DataCriacao dataCriacao;

    public DataCriacaoValidator(final DataCriacao dataCriacao, final ValidationHandler handler) {
        super(handler);
        this.dataCriacao = dataCriacao;
    }

    @Override
    public void validate() {
        checkConstraints();
    }

    private void checkConstraints() {
        final LocalDate value = this.dataCriacao.getValue();

        if (value == null) {
            this.validationHandler().append(new Error("Data de criação não deve ser nula"));
            return;
        }

        if (value.isAfter(LocalDate.now())) {
            this.validationHandler().append(new Error("Data de criação não pode ser no futuro"));
        }
    }
}
