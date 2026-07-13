package com.autoservice.domain.veiculo.validators;

import com.autoservice.domain.veiculo.valueobject.Placa;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

import static java.util.Objects.isNull;

public class PlacaValidator extends Validator {

    private static final String PLACA_MERCOSUL_PATTERN = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$";

    private static final String PLACA_ANTIGA_PATTERN = "^[A-Z]{3}-?[0-9]{4}$";

    private static final String PLACA_PATTERN =
            "^(?:" + PLACA_MERCOSUL_PATTERN + "|" + PLACA_ANTIGA_PATTERN + ")$";

    private static final String PLACA_NULA_MESSAGE = "Placa não deve ser nula";

    private static final String PLACA_INVALIDA_MESSAGE =
            "Placa inválida. Deve seguir o padrão Mercosul (ABC1D23) ou padrão antigo (ABC-1234)";

    private final Placa placa;

    public PlacaValidator(
            final Placa placa,
            final ValidationHandler handler
    ) {
        super(handler);
        this.placa = placa;
    }

    @Override
    public void validate() {
        if (isNull(placa) || isNull(placa.getValue())) {
            this.validationHandler()
                    .append(new Error(PLACA_NULA_MESSAGE));
            return;
        }

        final String placaNormalizada = placa.getValue().trim().toUpperCase();

        if (!placaNormalizada.matches(PLACA_PATTERN)) {
            this.validationHandler()
                    .append(new Error(PLACA_INVALIDA_MESSAGE));
        }
    }
}
