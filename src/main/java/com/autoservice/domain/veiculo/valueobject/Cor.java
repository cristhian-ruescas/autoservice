package com.autoservice.domain.veiculo.valueobject;

import com.autoservice.domain.ValidatedStringValueObject;
import com.autoservice.domain.veiculo.validators.CorValidator;
import com.autoservice.validation.ValidationHandler;

public class Cor extends ValidatedStringValueObject {

    protected Cor() {
        super();
    }

    private Cor(final String value) {
        super(value);
    }

    public static Cor from(final String value) {
        return validated(value, Cor::new);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new CorValidator(this, handler).validate();
    }
}
