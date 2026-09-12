package com.autoservice.domain.ordemcompra.validators;

import com.autoservice.domain.ordemcompra.OrdemCompra;
import com.autoservice.domain.ordemcompra.enums.OrdemCompraStatus;
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
class OrdemCompraValidatorTest {

    @Mock
    private OrdemCompra ordemCompra;

    @Test
    void statusNuloGeraErro() {
        when(ordemCompra.getStatus()).thenReturn(null);
        when(ordemCompra.getDataCompra()).thenReturn(DataCompra.from(LocalDate.now()));
        final var handler = new NotificationValidationHandler();
        new OrdemCompraValidator(ordemCompra, handler).validate();
        assertTrue(handler.hasError());
    }

    @Test
    void dataCompraNulaGeraErro() {
        when(ordemCompra.getStatus()).thenReturn(OrdemCompraStatus.PENDENTE);
        when(ordemCompra.getDataCompra()).thenReturn(null);
        final var handler = new NotificationValidationHandler();
        new OrdemCompraValidator(ordemCompra, handler).validate();
        assertTrue(handler.hasError());
    }

    @Test
    void validoNaoAcumulaErro() {
        when(ordemCompra.getStatus()).thenReturn(OrdemCompraStatus.PENDENTE);
        when(ordemCompra.getDataCompra()).thenReturn(DataCompra.from(LocalDate.now()));
        final var handler = new NotificationValidationHandler();
        new OrdemCompraValidator(ordemCompra, handler).validate();
        assertFalse(handler.hasError());
    }
}
