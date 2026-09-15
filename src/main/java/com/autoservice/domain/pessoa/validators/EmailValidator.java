package com.autoservice.domain.pessoa.validators;

import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

public class EmailValidator extends Validator {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final int MAX_LENGTH = 255;
    private static final String EMAIL_NULO_MESSAGE = "Email não deve ser nulo ou vazio";
    private static final String EMAIL_TAMANHO_MESSAGE = "Email não deve exceder " + MAX_LENGTH + " caracteres";
    private static final String EMAIL_FORMATO_MESSAGE = "Email deve estar em formato válido (ex: usuario@dominio.com)";
    private final Email email;

    public EmailValidator(final Email email, final ValidationHandler handler) {
        super(handler);
        this.email = email;
    }

    @Override
    public void validate() {
        checkConstraints();
    }

    private void checkConstraints() {
        final var value = this.email.getValue();

        if (value == null || value.isBlank()) {
            this.validationHandler().append(new Error(EMAIL_NULO_MESSAGE));
            return;
        }

        if (value.length() > MAX_LENGTH) {
            this.validationHandler().append(new Error(EMAIL_TAMANHO_MESSAGE));
            return;
        }

        if (!value.matches(EMAIL_REGEX)) {
            this.validationHandler().append(new Error(EMAIL_FORMATO_MESSAGE));
        }
    }
}
