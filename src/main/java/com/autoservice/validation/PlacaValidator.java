package com.autoservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PlacaValidator implements ConstraintValidator<ValidPlaca, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return value.matches("[A-Z]{3}[0-9][0-9A-Z][0-9]{2}");
    }
}

