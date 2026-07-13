package com.autoservice.domain.veiculo.validators;

import com.autoservice.domain.veiculo.valueobject.Cor;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

public class CorValidator extends Validator {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 30;
    private static final String MSG_NULA = "Cor do veículo não deve ser nula";
    private static final String MSG_VAZIA = "Cor do veículo não deve estar vazia";
    private static final String MSG_MIN_LENGTH = "Cor do veículo deve ter no mínimo %d caracteres";
    private static final String MSG_MAX_LENGTH = "Cor do veículo deve ter no máximo %d caracteres";
    private static final String MSG_CARACTER_INVALIDO = "Cor do veículo deve conter apenas letras e espaços";
    private final Cor cor;

    public CorValidator(
            final Cor cor,
            final ValidationHandler handler
    ) {
        super(handler);
        this.cor = cor;
    }

    @Override
    public void validate() {
        checkConstraints();
    }

    private void checkConstraints() {
        final String value = this.cor.getValue();

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
                    .append(new Error(MSG_CARACTER_INVALIDO));
        }
    }
}