package com.autoservice.domain.pessoa.valueobject;

import com.autoservice.domain.ValueObject;
import com.autoservice.domain.pessoa.validators.CPFValidator;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;

import java.util.Objects;

public class CPF extends ValueObject {

    private final String value;

    protected CPF() {
        this.value = null;
    }

    private CPF(final String value) {
        this.value = value;
    }

    public static CPF from(final String cpf) {
        final String normalized = normalize(cpf);

        final CPF newCpf = new CPF(normalized);

        newCpf.validate(new NotificationValidationHandler());

        return newCpf;
    }

    private static String normalize(final String cpf) {
        if (cpf == null) {
            return null;
        }

        return cpf.replaceAll("[^\\d]", "");
    }

    public void validate(ValidationHandler handler) {
        new CPFValidator(this, handler).validate();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final CPF that = (CPF) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
