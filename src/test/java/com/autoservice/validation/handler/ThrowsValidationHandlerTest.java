package com.autoservice.validation.handler;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThrowsValidationHandlerTest {

    @Test
    void appendDisparaDomainException() {
        final ThrowsValidationHandler h = new ThrowsValidationHandler();
        final var ex = assertThrows(DomainException.class, () -> h.append(new Error("e")));
        assertTrue(ex.getMessage().contains("e") || ex.getErrors().stream().anyMatch(e -> e.message().contains("e")));
    }

    @Test
    void appendHandlerDisparaDomainException() {
        final ThrowsValidationHandler h = new ThrowsValidationHandler();
        final NotificationValidationHandler inner = new NotificationValidationHandler();
        inner.append(new Error("x"));
        assertThrows(DomainException.class, () -> h.append(inner));
    }

    @Test
    void validateSucesso() {
        final ThrowsValidationHandler h = new ThrowsValidationHandler();
        final String r = h.validate(() -> "ok");
        assertEquals("ok", r);
    }

    @Test
    void validateFalha() {
        final ThrowsValidationHandler h = new ThrowsValidationHandler();
        assertThrows(DomainException.class, () ->
                h.validate(() -> {
                    throw new IllegalStateException("falhou");
                }));
    }

    @Test
    void getErrorsVazio() {
        assertTrue(new ThrowsValidationHandler().getErrors().isEmpty());
    }

    @Test
    void defaultsHasErrorFirstError_viaInterface() {
        ValidationHandler h = new ThrowsValidationHandler();
        assertFalse(h.hasError());
        assertNull(h.firstError());
    }

    @Test
    void notificationComoValidationHandler_primeiroErro() {
        NotificationValidationHandler n = new NotificationValidationHandler();
        ValidationHandler h = n;
        n.append(new Error("um"));
        assertTrue(h.hasError());
        assertEquals("um", h.firstError().message());
    }
}
