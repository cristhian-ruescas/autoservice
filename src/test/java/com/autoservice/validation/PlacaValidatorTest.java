package com.autoservice.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class PlacaValidatorTest {
    private final PlacaValidator validator = new PlacaValidator();
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

    @Test
    void testValidPlacas() {
        assertTrue(validator.isValid("ABC1234", context));
        assertTrue(validator.isValid("BRA2E19", context));
        assertTrue(validator.isValid("XYZ9A99", context));
    }

    @Test
    void testInvalidPlacas() {
        assertFalse(validator.isValid("AB12345", context));
        assertFalse(validator.isValid("ABCDE12", context));
        assertFalse(validator.isValid("1234567", context));
        assertFalse(validator.isValid("ABC1E1", context));
        assertFalse(validator.isValid("abc1234", context));
        assertFalse(validator.isValid(null, context));
        assertFalse(validator.isValid("", context));
    }
}
