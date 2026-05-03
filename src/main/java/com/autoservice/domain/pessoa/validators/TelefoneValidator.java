package com.autoservice.domain.pessoa.validators;

import com.autoservice.domain.pessoa.valueobject.Telefone;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

public class TelefoneValidator extends Validator {

    private final Telefone telefone;

    private static final String TELEFONE_PATTERN = "\\d{10,11}";

    private static final String TELEFONE_NULO_MESSAGE =
            "Telefone não deve ser nulo ou vazio";

    private static final String TELEFONE_FORMATO_MESSAGE =
            "Telefone deve conter DDD válido e 10 ou 11 dígitos";

    public TelefoneValidator(
            final Telefone telefone,
            final ValidationHandler handler
    ) {
        super(handler);
        this.telefone = telefone;
    }

    @Override
    public void validate() {
        checkConstraints();
    }

    private void checkConstraints() {
        final String value = this.telefone.getValue();

        if (value == null || value.isBlank()) {
            this.validationHandler()
                    .append(new Error(TELEFONE_NULO_MESSAGE));
            return;
        }

        if (!value.matches(TELEFONE_PATTERN)) {
            this.validationHandler()
                    .append(new Error(TELEFONE_FORMATO_MESSAGE));
            return;
        }

        validateDDD(value);
    }

    public void validateDDD(final String value) {
        final int ddd = Integer.parseInt(value.substring(0, 2));

        if (ddd < 11) {
            this.validationHandler()
                    .append(new Error(TELEFONE_FORMATO_MESSAGE));
        }
    }
}