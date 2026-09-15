package com.autoservice.domain.veiculo.valueobject;

import com.autoservice.domain.ValidatedStringValueObject;
import com.autoservice.domain.veiculo.validators.MarcaValidator;
import com.autoservice.validation.ValidationHandler;

public class Marca extends ValidatedStringValueObject {

    protected Marca() {
        super();
    }

    private Marca(final String value) {
        super(value);
    }

    public static Marca from(final String value) {
        return validated(value, Marca::new);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new MarcaValidator(this, handler).validate();
    }
}
