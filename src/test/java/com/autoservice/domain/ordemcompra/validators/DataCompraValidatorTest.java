package com.autoservice.domain.ordemcompra.validators;

import com.autoservice.domain.ordemcompra.valueobject.DataCompra;
import com.autoservice.validation.handler.NotificationValidationHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataCompraValidatorTest {

    @Mock
    private DataCompra dataFutura;

    @Test
    void dataFuturaGeraErro() {
        when(dataFutura.getValue()).thenReturn(LocalDate.now().plusDays(1));
        final var handler = new NotificationValidationHandler();
        new DataCompraValidator(dataFutura, handler).validate();
        assertTrue(handler.hasError());
    }

    @Test
    void dataAtualSemErro() {
        final DataCompra data = DataCompra.from(LocalDate.now());
        final var handler = new NotificationValidationHandler();
        new DataCompraValidator(data, handler).validate();
        assertFalse(handler.hasError());
    }
}
