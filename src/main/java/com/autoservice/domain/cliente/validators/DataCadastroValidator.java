package com.autoservice.domain.cliente.validators;

import com.autoservice.domain.cliente.valueobject.DataCadastro;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

import java.time.LocalDate;

public class DataCadastroValidator extends Validator {

    private static final int MAX_ANOS_PASSADO = 10;
    private static final int MAX_DIAS_FUTURO = 0;
    private final DataCadastro dataCadastro;

    public DataCadastroValidator(
            final DataCadastro dataCadastro,
            final ValidationHandler handler
    ) {
        super(handler);
        this.dataCadastro = dataCadastro;
    }

    @Override
    public void validate() {
        checkConstraints();
    }

    private void checkConstraints() {
        final LocalDate value = this.dataCadastro.getValue();

        if (value == null) {
            this.validationHandler()
                    .append(new Error(
                            "Data de cadastro não deve ser nula"
                    ));
            return;
        }

        final LocalDate hoje = LocalDate.now();

        if (value.isBefore(hoje.minusYears(MAX_ANOS_PASSADO))) {
            this.validationHandler()
                    .append(new Error(
                            "Data de cadastro não pode ser anterior a "
                                    + MAX_ANOS_PASSADO + " anos"
                    ));
        }

        if (value.isAfter(hoje.plusDays(MAX_DIAS_FUTURO))) {
            this.validationHandler()
                    .append(new Error(
                            "Data de cadastro não pode ser no futuro"
                    ));
        }
    }

}