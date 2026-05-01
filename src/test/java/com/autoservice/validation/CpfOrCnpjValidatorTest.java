package com.autoservice.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class CpfOrCnpjValidatorTest {
    private final CpfOrCnpjValidator validator = new CpfOrCnpjValidator();
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

    @Test
    void testValidCpf() {
        assertTrue(validator.isValid("388.558.640-17", context));
        assertTrue(validator.isValid("38855864017", context));
    }

    @Test
    void testInvalidCpf() {
        assertFalse(validator.isValid("123.456.789-00", context));
        assertFalse(validator.isValid("11111111111", context));
        assertFalse(validator.isValid("123", context));
    }

    @Test
    void testValidCnpj() {
        assertTrue(validator.isValid("04.252.011/0001-10", context));
        assertTrue(validator.isValid("04252011000110", context));
    }

    @Test
    void testInvalidCnpj() {
        assertFalse(validator.isValid("00.000.000/0000-00", context));
        assertFalse(validator.isValid("12345678000100", context));
        assertFalse(validator.isValid("11111111111111", context));
        assertFalse(validator.isValid("123", context));
    }

    @Test
    void testNullOrEmpty() {
        assertFalse(validator.isValid(null, context));
        assertFalse(validator.isValid("", context));
    }
}

