package com.autoservice.domain.ordemservico.validators;

import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

import static java.util.Objects.isNull;

public class OrdemServicoValidator extends Validator {

    private final OrdemServico ordemServico;

    public OrdemServicoValidator(final OrdemServico ordemServico, final ValidationHandler handler) {
        super(handler);
        this.ordemServico = ordemServico;
    }

    @Override
    public void validate() {
        validateVeiculoId();
        validateStatus();
        validateDataCriacao();
        validateRelato();
    }

    private void validateVeiculoId() {
        if (isNull(ordemServico.getVeiculoId())) {
            this.validationHandler().append(new Error("Veículo ID não deve ser nulo"));
        }
    }

    private void validateStatus() {
        if (isNull(ordemServico.getStatus())) {
            this.validationHandler().append(new Error("Status da ordem de serviço não deve ser nulo"));
        }
    }

    private void validateDataCriacao() {
        if (isNull(ordemServico.getDataCriacao())) {
            this.validationHandler().append(new Error("Data de criação não deve ser nula"));
        } else {
            ordemServico.getDataCriacao().validate(this.validationHandler());
        }
    }

    private void validateRelato() {
        final String relato = ordemServico.getRelato();

        if (isNull(relato) || relato.isBlank()) {
            this.validationHandler().append(new Error("Relato do cliente não deve ser nulo ou vazio"));
            return;
        }

        if (relato.trim().length() > 1000) {
            this.validationHandler().append(new Error("Relato do cliente não deve exceder 1000 caracteres"));
        }
    }
}
