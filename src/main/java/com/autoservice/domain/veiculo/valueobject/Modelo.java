package com.autoservice.domain.veiculo.valueobject;

import com.autoservice.domain.ValidatedStringValueObject;
import com.autoservice.domain.veiculo.validators.ModeloValidator;
import com.autoservice.validation.ValidationHandler;

public class Modelo extends ValidatedStringValueObject {

    protected Modelo() {
        super();
    }

    private Modelo(final String value) {
        super(value);
    }

    public static Modelo from(final String value) {
        return validated(value, Modelo::new);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new ModeloValidator(this, handler).validate();
    }
}
