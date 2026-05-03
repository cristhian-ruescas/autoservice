package com.autoservice.domain.pessoa.validators;

import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;

public class PessoaJuridicaValidator extends PessoaValidator {

    private static final String RAZAO_SOCIAL_NULA_MESSAGE = "Razão Social não pode ser nula ou vazia";
    private static final String RAZAO_SOCIAL_TAMANHO_MESSAGE = "Razão Social deve possuir entre 3 e 255 caracteres";
    private static final String REPRESENTANTE_NULO_MESSAGE = "Representante Legal não deve ser nulo";

    public PessoaJuridicaValidator(final PessoaJuridica pessoa, final ValidationHandler aHandler) {
        super(pessoa, aHandler);
    }

    @Override
    protected void validateSpecificFields() {
        PessoaJuridica pj = (PessoaJuridica) this.pessoa;
        validateRazaoSocial(pj);
        validateCNPJ(pj);
        validateRepresentanteLegal(pj);
    }

    public void validateRazaoSocial(PessoaJuridica pessoa) {
        final var razaoSocial = pessoa.getRazaoSocial();
        if (razaoSocial == null || razaoSocial.isBlank()) {
            this.validationHandler().append(new Error(RAZAO_SOCIAL_NULA_MESSAGE));
            return;
        }

        int length = razaoSocial.trim().length();
        if (length > 255 || length < 3) {
            this.validationHandler().append(new Error(RAZAO_SOCIAL_TAMANHO_MESSAGE));
        }
    }

    public void validateCNPJ(PessoaJuridica pessoa) {
        if (pessoa.getCnpj() != null) {
            new CNPJValidator(this.validationHandler(), pessoa.getCnpj()).validate();
        }
    }

    public void validateRepresentanteLegal(PessoaJuridica pessoa) {
        if (pessoa.getRepresentanteLegalId() == null) {
            this.validationHandler().append(new Error(REPRESENTANTE_NULO_MESSAGE));
        }
    }
}
