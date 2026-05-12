package com.autoservice.domain.veiculo.validators;

import com.autoservice.domain.veiculo.valueobject.Marca;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

public class MarcaValidator extends Validator {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 50;
    private static final String MSG_NULA = "Marca não deve ser nula";
    private static final String MSG_VAZIA = "Marca não deve estar vazia";
    private static final String MSG_MIN_LENGTH = "Marca deve ter no mínimo %d caracteres";
    private static final String MSG_MAX_LENGTH = "Marca deve ter no máximo %d caracteres";
    private static final String MSG_CARACTERES_INVALIDOS = "Marca deve conter apenas letras e espaços";
    private final Marca marca;

    public MarcaValidator(
            final Marca marca,
            final ValidationHandler handler
    ) {
        super(handler);
        this.marca = marca;
    }

    @Override
    public void validate() {
        checkConstraints();
    }

    private void checkConstraints() {
        final String value = this.marca.getValue();

        if (value == null) {
            this.validationHandler()
                    .append(new Error(MSG_NULA));
            return;
        }

        final String trimmed = value.trim();

        if (trimmed.isEmpty()) {
            this.validationHandler()
                    .append(new Error(MSG_VAZIA));
            return;
        }

        if (trimmed.length() < MIN_LENGTH) {
            this.validationHandler()
                    .append(new Error(
                            String.format(MSG_MIN_LENGTH, MIN_LENGTH)
                    ));
        }

        if (trimmed.length() > MAX_LENGTH) {
            this.validationHandler()
                    .append(new Error(
                            String.format(MSG_MAX_LENGTH, MAX_LENGTH)
                    ));
        }

        if (!trimmed.matches("^[\\p{L} ]+$")) {
            this.validationHandler()
                    .append(new Error(MSG_CARACTERES_INVALIDOS));
        }
    }
}