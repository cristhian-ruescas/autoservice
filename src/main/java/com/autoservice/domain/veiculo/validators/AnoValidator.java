package com.autoservice.domain.veiculo.validators;

import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

import java.time.LocalDate;

public class AnoValidator extends Validator {

    private static final int ANO_MINIMO = 1886;
    private static final String MSG_NULO = "Ano não deve ser nulo";
    private static final String MSG_MINIMO = "Ano não pode ser menor que %d";
    private static final String MSG_MAXIMO = "Ano não pode ser maior que %d";
    private final Ano ano;

    public AnoValidator(
            final Ano ano,
            final ValidationHandler handler
    ) {
        super(handler);
        this.ano = ano;
    }

    @Override
    public void validate() {
        checkConstraints();
    }

    private void checkConstraints() {
        final Integer value = this.ano.getValue();

        if (value == null) {
            this.validationHandler()
                    .append(new Error(MSG_NULO));
            return;
        }

        final int anoAtual = LocalDate.now().getYear();
        final int anoMaximo = anoAtual + 1;

        if (value < ANO_MINIMO) {
            this.validationHandler()
                    .append(new Error(
                            String.format(MSG_MINIMO, ANO_MINIMO)
                    ));
        }

        if (value > anoMaximo) {
            this.validationHandler()
                    .append(new Error(
                            String.format(MSG_MAXIMO, anoMaximo)
                    ));
        }
    }
}