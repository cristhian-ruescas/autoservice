package com.autoservice.domain.pessoa.valueobject;

import com.autoservice.domain.ValueObject;
import com.autoservice.domain.pessoa.validators.TelefoneValidator;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;

import java.util.Objects;

public class Telefone extends ValueObject {

    private final String valor;

    protected Telefone() {
        this.valor = null;
    }

    private Telefone(final String valor) {
        this.valor = valor;
    }

    public static Telefone from(final String phone) {
        final String normalized = normalize(phone);
        final Telefone newPhone = new Telefone(normalized);

        newPhone.validate(new NotificationValidationHandler());

        return newPhone;
    }

    private static String normalize(final String phone) {
        if (phone == null) {
            return null;
        }

        return phone
                .trim()
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")
                .replace(".", "")
                .replace(" ", "");
    }

    public void validate(ValidationHandler handler) {
        new TelefoneValidator(this, handler).validate();
    }

    public String getValue() {
        return valor;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final Telefone that = (Telefone) o;
        return Objects.equals(valor, that.valor);
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
