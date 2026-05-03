package com.autoservice.domain.pessoa.validators;

import com.autoservice.domain.pessoa.valueobject.CNPJ;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

import java.util.List;
import java.util.regex.Pattern;

public class CNPJValidator extends Validator {

    private final CNPJ cnpj;

    private static final int TAMANHO_CNPJ_SEM_DV = 12;
    private static final int VALOR_BASE = '0';

    private static final String RAW_REGEX = "[A-Za-z0-9]{14}";
    private static final String FORMATTED_REGEX =
            "[A-Za-z0-9]{2}\\.[A-Za-z0-9]{3}\\.[A-Za-z0-9]{3}/[A-Za-z0-9]{4}-\\d{2}";

    private static final String CNPJ_NULO_MESSAGE = "CNPJ não deve ser nulo ou vazio";
    private static final String CNPJ_FORMATO_MESSAGE = "CNPJ deve estar no formato válido (XX.XXX.XXX/XXXX-XX ou XXXXXXXXXXXXXX)";
    private static final String CNPJ_INVALIDO_MESSAGE = "CNPJ inválido";

    private static final int[] PESOS_DV =
            {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private static final List<String> ALL_CNPJ_REGEX = List.of(
            RAW_REGEX,
            FORMATTED_REGEX
    );

    public CNPJValidator(
            final ValidationHandler aHandler,
            final CNPJ cnpj
    ) {
        super(aHandler);
        this.cnpj = cnpj;
    }

    @Override
    public void validate() {
        checkConstraints();
    }

    private void checkConstraints() {
        final String value = this.cnpj.getValue();

        if (value == null || value.isBlank()) {
            this.validationHandler()
                    .append(new Error(CNPJ_NULO_MESSAGE));
            return;
        }

        if (!isCnpj(value)) {
            this.validationHandler()
                    .append(new Error(CNPJ_FORMATO_MESSAGE));
            return;
        }

        if (!isValidCnpj(value)) {
            this.validationHandler()
                    .append(new Error(CNPJ_INVALIDO_MESSAGE));
        }
    }

    public static boolean isCnpj(String input) {
        if (input == null) {
            return false;
        }

        return ALL_CNPJ_REGEX.stream()
                .anyMatch(regex -> Pattern.matches(regex, input));
    }

    public static boolean isValidCnpj(String input) {
        if (!isCnpj(input)) {
            return false;
        }

        final String rawCnpj = input.replaceAll("[^A-Za-z0-9]", "");

        final String dvInformado =
                rawCnpj.substring(TAMANHO_CNPJ_SEM_DV);

        final String dvCalculado =
                calculaDV(
                        rawCnpj.substring(0, TAMANHO_CNPJ_SEM_DV)
                );

        return dvCalculado.equals(dvInformado);
    }

    private static String calculaDV(String cnpj) {
        final String dv1 =
                String.valueOf(calculaDigito(cnpj));

        final String dv2 =
                String.valueOf(
                        calculaDigito(cnpj + dv1)
                );

        return dv1 + dv2;
    }

    private static int calculaDigito(String cnpj) {
        int soma = 0;

        for (int indice = cnpj.length() - 1; indice >= 0; indice--) {
            final int valorCaracter =
                    cnpj.charAt(indice) - VALOR_BASE;

            soma += valorCaracter *
                    PESOS_DV[
                            PESOS_DV.length
                                    - cnpj.length()
                                    + indice
                            ];
        }

        return (soma % 11 < 2)
                ? 0
                : 11 - (soma % 11);
    }
}