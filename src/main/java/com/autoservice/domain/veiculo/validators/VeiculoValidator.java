package com.autoservice.domain.veiculo.validators;

import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

import static java.util.Objects.isNull;

public class VeiculoValidator extends Validator {

    private static final String PROPRIETARIO_NULO_MESSAGE = "Proprietário ID não deve ser nulo";
    private static final String TIPO_VEICULO_NULO_MESSAGE = "Tipo de veículo ID não deve ser nulo";

    private final Veiculo veiculo;

    public VeiculoValidator(final Veiculo veiculo, final ValidationHandler handler) {
        super(handler);
        this.veiculo = veiculo;
    }

    @Override
    public void validate() {
        validateProprietarioId();
        validateTipoVeiculoId();
        validatePlaca();
        validateCor();
        validateKilometragem();
    }

    private void validateProprietarioId() {
        if (isNull(veiculo.getProprietarioId())) {
            this.validationHandler().append(new Error(PROPRIETARIO_NULO_MESSAGE));
        }
    }

    private void validatePlaca() {
        if (isNull(veiculo.getPlaca())) {
            this.validationHandler().append(new Error("Placa não deve ser nula"));
        } else {
            veiculo.getPlaca().validate(this.validationHandler());
        }
    }

    private void validateTipoVeiculoId() {
        if (isNull(veiculo.getTipoVeiculoId())) {
            this.validationHandler().append(new Error(TIPO_VEICULO_NULO_MESSAGE));
        }
    }

    private void validateCor() {
        if (isNull(veiculo.getCor())) {
            this.validationHandler().append(new Error("Cor do veículo não deve ser nula"));
        } else {
            veiculo.getCor().validate(this.validationHandler());
        }
    }

    private void validateKilometragem() {
        if (isNull(veiculo.getKilometragem())) {
            this.validationHandler().append(new Error("Kilometragem não deve ser nula"));
        } else {
            veiculo.getKilometragem().validate(this.validationHandler());
        }
    }
}
