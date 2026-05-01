package com.autoservice.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CnpjValidatorTest {
    private final CnpjValidator validator = new CnpjValidator();
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

    @Test
    void testValidCnpj() {
        // CNPJs válidos
        assertTrue(validator.isValid("04.252.011/0001-10", context));
        assertTrue(validator.isValid("04252011000110", context));
        assertTrue(validator.isValid("40.688.134/0001-61", context));
    }

    @Test
    void testInvalidCnpj() {
        // CNPJs inválidos
        assertFalse(validator.isValid("00.000.000/0000-00", context)); // todos iguais
        assertFalse(validator.isValid("12345678000195", context)); // dígito verificador errado
        assertFalse(validator.isValid("11111111111111", context)); // todos iguais
        assertFalse(validator.isValid("123", context)); // muito curto
        assertFalse(validator.isValid("abcdefghijklmno", context)); // não numérico
        assertFalse(validator.isValid(null, context)); // nulo
        assertFalse(validator.isValid("", context)); // vazio
    }
}

