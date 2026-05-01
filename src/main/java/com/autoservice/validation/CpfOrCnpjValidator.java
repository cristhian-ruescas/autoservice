package com.autoservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfOrCnpjValidator implements ConstraintValidator<ValidCpfOrCnpj, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        String digits = value.replaceAll("\\D", "");
        if (digits.length() == 11) {
            return new CpfValidator().isValid(value, context);
        } else if (digits.length() == 14) {
            return new CnpjValidator().isValid(value, context);
        }
        return false;
    }
}
