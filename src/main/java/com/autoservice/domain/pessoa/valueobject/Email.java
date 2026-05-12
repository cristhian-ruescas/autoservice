package com.autoservice.domain.pessoa.valueobject;

import com.autoservice.domain.ValueObject;
import com.autoservice.domain.pessoa.validators.EmailValidator;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Email extends ValueObject {

    @Column(
            name = "email",
            nullable = false,
            updatable = false
    )
    private final String valor;

    protected Email() {
        this.valor = null;
    }

    private Email(final String valor) {
        this.valor = valor;
    }

    public static Email from(final String email) {
        final String normalized = normalize(email);

        final Email newEmail = new Email(normalized);

        newEmail.validate(new NotificationValidationHandler());

        return newEmail;
    }

    private static String normalize(final String email) {
        if (email == null) {
            return null;
        }

        return email.trim().toLowerCase();
    }

    public void validate(ValidationHandler handler) {
        new EmailValidator(this, handler).validate();
    }

    public String getValue() {
        return valor;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final Email email = (Email) o;
        return Objects.equals(valor, email.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
