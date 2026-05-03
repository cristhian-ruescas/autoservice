package com.autoservice.domain.veiculo.validators;

import com.autoservice.domain.veiculo.valueobject.Modelo;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

public class ModeloValidator extends Validator {

    private final Modelo modelo;

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 50;

    private static final String MSG_NULO = "Modelo não deve ser nulo";
    private static final String MSG_VAZIO = "Modelo não deve estar vazio";
    private static final String MSG_MIN_LENGTH = "Modelo deve ter no mínimo %d caracteres";
    private static final String MSG_MAX_LENGTH = "Modelo deve ter no máximo %d caracteres";
    private static final String MSG_CARACTER_INVALIDO = "Modelo contém caracteres inválidos";

    public ModeloValidator(
            final Modelo modelo,
            final ValidationHandler handler
    ) {
        super(handler);
        this.modelo = modelo;
    }

    @Override
    public void validate() {
        checkConstraints();
    }

    private void checkConstraints() {
        final String value = this.modelo.getValue();

        if (value == null) {
            this.validationHandler()
                    .append(new Error(MSG_NULO));
            return;
        }

        final String trimmed = value.trim();

        if (trimmed.isEmpty()) {
            this.validationHandler()
                    .append(new Error(MSG_VAZIO));
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

        if (!trimmed.matches("^[\\p{L}0-9 .\\-]+$")) {
            this.validationHandler()
                    .append(new Error(MSG_CARACTER_INVALIDO));
        }
    }
}