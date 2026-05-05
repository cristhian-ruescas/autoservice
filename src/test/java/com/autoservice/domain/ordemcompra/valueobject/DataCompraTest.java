package com.autoservice.domain.ordemcompra.valueobject;

import com.autoservice.domain.exceptions.DomainException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DataCompraTest {

    @Test
    void fromDataValida() {
        final LocalDate hoje = LocalDate.now();
        final DataCompra d = DataCompra.from(hoje);
        assertEquals(hoje, d.getValue());
    }

    @Test
    void dataFuturaFalha() {
        assertThrows(DomainException.class, () ->
                DataCompra.from(LocalDate.now().plusDays(1)));
    }
}
