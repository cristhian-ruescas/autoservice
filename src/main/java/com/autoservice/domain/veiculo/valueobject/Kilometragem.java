package com.autoservice.domain.veiculo.valueobject;

import com.autoservice.domain.ValidatedIntegerValueObject;
import com.autoservice.domain.veiculo.validators.KilometragemValidator;
import com.autoservice.validation.ValidationHandler;

public class Kilometragem extends ValidatedIntegerValueObject {

    protected Kilometragem() {
        super();
    }

    private Kilometragem(final Integer value) {
        super(value);
    }

    public static Kilometragem from(final Integer value) {
        return validated(value, Kilometragem::new);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new KilometragemValidator(this, handler).validate();
    }
}
