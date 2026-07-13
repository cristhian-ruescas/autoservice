package com.autoservice.application.servico.create;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.servico.Servico;
import com.autoservice.domain.servico.ServicoGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastrarServicoUseCaseTest {

    @Mock
    private ServicoGateway servicoGateway;

    @InjectMocks
    private CadastrarServicoUseCase useCase;

    @Test
    @DisplayName("Cadastra serviço e persiste via gateway")
    void cadastra() {
        when(this.servicoGateway.create(any(Servico.class))).thenAnswer(inv -> inv.getArgument(0));

        final var output = this.useCase.execute(CadastrarServicoCommand.with(
                "Alinhamento",
                "Alinhamento e balanceamento",
                new BigDecimal("120.00")
        ));

        assertEquals("Alinhamento", output.nome());
        assertEquals(new BigDecimal("120.00"), output.valorReferencia());
        verify(this.servicoGateway).create(any(Servico.class));
    }

    @Test
    @DisplayName("Nome vazio falha no domínio")
    void nomeInvalido() {
        assertThrows(DomainException.class, () -> this.useCase.execute(CadastrarServicoCommand.with(
                "  ",
                null,
                null
        )));
    }
}
