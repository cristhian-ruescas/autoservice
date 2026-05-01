package com.autoservice.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class CnpjValidatorTest {
    private final CnpjValidator validator = new CnpjValidator();
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

    @Test
    void testValidCnpj() {
        assertTrue(validator.isValid("04.252.011/0001-10", context));
        assertTrue(validator.isValid("04252011000110", context));
        assertTrue(validator.isValid("40.688.134/0001-61", context));
    }

    @Test
    void testInvalidCnpj() {
        assertFalse(validator.isValid("00.000.000/0000-00", context));
        assertFalse(validator.isValid("12345678000100", context));
        assertFalse(validator.isValid("11111111111111", context));
        assertFalse(validator.isValid("123", context));
        assertFalse(validator.isValid("abcdefghijklmno", context));
        assertFalse(validator.isValid(null, context));
        assertFalse(validator.isValid("", context));
    }
}
