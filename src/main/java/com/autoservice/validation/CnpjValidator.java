package com.autoservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CnpjValidator implements ConstraintValidator<ValidCNPJ, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        String cnpj = value.replaceAll("\\D", "");
        if (cnpj.length() != 14 || cnpj.chars().distinct().count() == 1) return false;
        try {
            int[] peso1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int d1 = 0, d2 = 0;
            for (int i = 0; i < 12; i++) d1 += (cnpj.charAt(i) - '0') * peso1[i];
            d1 = d1 % 11 < 2 ? 0 : 11 - d1 % 11;
            for (int i = 0; i < 12; i++) d2 += (cnpj.charAt(i) - '0') * peso2[i];
            d2 += d1 * peso2[12];
            d2 = d2 % 11 < 2 ? 0 : 11 - d2 % 11;
            return d1 == (cnpj.charAt(12) - '0') && d2 == (cnpj.charAt(13) - '0');
        } catch (Exception e) {
            return false;
        }
    }
}

