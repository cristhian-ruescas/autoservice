package com.autoservice.domain.veiculo.valueobject;

import com.autoservice.domain.ValidatedStringValueObject;
import com.autoservice.domain.veiculo.validators.PlacaValidator;
import com.autoservice.validation.ValidationHandler;

public class Placa extends ValidatedStringValueObject {

    protected Placa() {
        super();
    }

    private Placa(final String value) {
        super(value);
    }

    public static Placa from(final String valor) {
        return validated(valor, Placa::new);
    }

    @Override
    protected String normalize(final String rawValue) {
        return rawValue == null ? null : rawValue.trim().toUpperCase();
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new PlacaValidator(this, handler).validate();
    }
}
