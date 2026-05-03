package com.autoservice.domain.pessoa.valueobject;

import com.autoservice.domain.ValueObject;
import com.autoservice.domain.pessoa.validators.CNPJValidator;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class CNPJ extends ValueObject {

    @Column(name = "cnpj")
    private final String valor;


    protected CNPJ() {
        this.valor = null;
    }

    private CNPJ(final String valor) {
        this.valor = valor;
    }

    public static CNPJ from(final String cnpj) {
        final String normalized = normalize(cnpj);

        final CNPJ newCnpj = new CNPJ(normalized);

        newCnpj.validate(new NotificationValidationHandler());

        return newCnpj;
    }

    private static String normalize(final String cnpj) {
        if (cnpj == null) {
            return null;
        }

        return cnpj.replaceAll("[^\\d]", "");
    }

    public void validate(ValidationHandler handler) {
        new CNPJValidator(handler, this).validate();
    }

    public String getValue() {
        return valor;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final CNPJ that = (CNPJ) o;
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
