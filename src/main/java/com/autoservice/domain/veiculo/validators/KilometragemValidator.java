package com.autoservice.domain.veiculo.validators;

import com.autoservice.domain.veiculo.valueobject.Kilometragem;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

public class KilometragemValidator extends Validator {

    private static final long KM_MINIMO = 0L;
    private static final String MSG_NULA = "Kilometragem não deve ser nula";
    private static final String MSG_NEGATIVA = "Kilometragem não pode ser negativa";
    private final Kilometragem kilometragem;

    public KilometragemValidator(
            final Kilometragem kilometragem,
            final ValidationHandler handler
    ) {
        super(handler);
        this.kilometragem = kilometragem;
    }

    @Override
    public void validate() {
        checkConstraints();
    }

    private void checkConstraints() {
        final Integer value = this.kilometragem.getValue();

        if (value == null) {
            this.validationHandler()
                    .append(new Error(MSG_NULA));
            return;
        }

        if (value < KM_MINIMO) {
            this.validationHandler()
                    .append(new Error(MSG_NEGATIVA));
        }
    }
}