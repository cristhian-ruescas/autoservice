package com.autoservice.domain.ordemservico.validators;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.valueobject.DataCriacao;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("DataCriacaoValidator")
class DataCriacaoValidatorTest {

    @Test
    @DisplayName("Deve falhar ao validar DataCriacao nula passando null ao validador diretamente")
    void deveRejeitarDataCriacaoNula() {
        DataCriacao dataCriacao = null;
        try {
            var constructor = DataCriacao.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            dataCriacao = constructor.newInstance();
        } catch (Exception e) {
        }

        final DataCriacao dataFinal = dataCriacao;
        var exception = assertThrows(DomainException.class, () ->
                new DataCriacaoValidator(dataFinal, new ThrowsValidationHandler()).validate()
        );

        assertEquals("Data de criação não deve ser nula", exception.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve falhar ao validar DataCriacao com data futura no validador")
    void deveRejeitarDataCriacaoFutura() {
        final LocalDate dataFutura = LocalDate.now().plusDays(2);

        var exception = assertThrows(DomainException.class, () -> DataCriacao.from(dataFutura));

        assertEquals("Data de criação não pode ser no futuro", exception.getErrors().getFirst().message());
    }
}
