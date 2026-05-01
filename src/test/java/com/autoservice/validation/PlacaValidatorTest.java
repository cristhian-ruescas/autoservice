package com.autoservice.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PlacaValidatorTest {
    private final PlacaValidator validator = new PlacaValidator();
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

    @Test
    void testValidPlacas() {
        assertTrue(validator.isValid("ABC1234", context)); // placa antiga
        assertTrue(validator.isValid("BRA2E19", context)); // placa Mercosul
        assertTrue(validator.isValid("XYZ9A99", context)); // placa Mercosul
    }

    @Test
    void testInvalidPlacas() {
        assertFalse(validator.isValid("AB12345", context)); // menos letras
        assertFalse(validator.isValid("ABCDE12", context)); // mais letras
        assertFalse(validator.isValid("1234567", context)); // só números
        assertFalse(validator.isValid("ABC1E1", context)); // menos caracteres
        assertFalse(validator.isValid("abc1234", context)); // minúsculas
        assertFalse(validator.isValid(null, context)); // nulo
        assertFalse(validator.isValid("", context)); // vazio
    }
}

