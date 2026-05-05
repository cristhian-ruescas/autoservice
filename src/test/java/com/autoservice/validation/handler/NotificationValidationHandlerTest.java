package com.autoservice.validation.handler;

import com.autoservice.validation.Error;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationValidationHandlerTest {

    @Test
    void appendErrorAcumula() {
        NotificationValidationHandler h = new NotificationValidationHandler();
        h.append(new Error("a")).append(new Error("b"));
        assertEquals(2, h.getErrors().size());
        assertTrue(h.hasError());
    }

    @Test
    void appendHandlerCopiaErros() {
        NotificationValidationHandler a = new NotificationValidationHandler();
        a.append(new Error("x"));
        NotificationValidationHandler b = new NotificationValidationHandler();
        b.append(a);
        assertEquals(1, b.getErrors().size());
    }

    @Test
    void validateExcecaoArmazenaErroERetornaNull() {
        NotificationValidationHandler h = new NotificationValidationHandler();
        String r = h.validate(() -> {
            throw new IllegalArgumentException("bad");
        });
        assertNull(r);
        assertEquals("bad", h.getErrors().getFirst().message());
    }

    @Test
    void validateSucessoRetornaValor() {
        NotificationValidationHandler h = new NotificationValidationHandler();
        Integer n = h.validate(() -> 42);
        assertEquals(42, n);
        assertTrue(h.getErrors().isEmpty());
    }
}
