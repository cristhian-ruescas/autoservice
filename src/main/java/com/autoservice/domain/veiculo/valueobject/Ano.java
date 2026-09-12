package com.autoservice.domain.veiculo.valueobject;

import com.autoservice.domain.ValidatedIntegerValueObject;
import com.autoservice.domain.veiculo.validators.AnoValidator;
import com.autoservice.validation.ValidationHandler;

public class Ano extends ValidatedIntegerValueObject {

    protected Ano() {
        super();
    }

    private Ano(final Integer value) {
        super(value);
    }

    public static Ano from(final Integer ano) {
        return validated(ano, Ano::new);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new AnoValidator(this, handler).validate();
    }
}
