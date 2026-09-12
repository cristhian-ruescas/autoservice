package com.autoservice.application.servico.update;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.servico.Servico;
import com.autoservice.domain.servico.ServicoGateway;
import com.autoservice.domain.servico.ServicoID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AtualizarServicoUseCase")
class AtualizarServicoUseCaseTest {

    @Mock
    private ServicoGateway servicoGateway;

    @InjectMocks
    private AtualizarServicoUseCase useCase;

    @Test
    void atualizaServicoComSucesso() {
        final var id = ServicoID.unique();
        final var atual = Servico.with(id, "Alinhamento", "Atual", new BigDecimal("100.00"));
        when(servicoGateway.findById(id)).thenReturn(Optional.of(atual));
        when(servicoGateway.update(any(Servico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var output = useCase.execute(AtualizarServicoCommand.with(
                UUID.fromString(id.getValue()),
                "Alinhamento Premium",
                "Descrição nova",
                new BigDecimal("150.00")
        ));

        assertEquals(id.getValue(), output.id());
        assertEquals("Alinhamento Premium", output.nome());
        assertEquals("Descrição nova", output.descricao());
        assertEquals(new BigDecimal("150.00"), output.valorReferencia());
        verify(servicoGateway).update(any(Servico.class));
    }

    @Test
    void mantemCamposAtuaisQuandoNaoInformados() {
        final var id = ServicoID.unique();
        final var atual = Servico.with(id, "Alinhamento", "Atual", new BigDecimal("100.00"));
        when(servicoGateway.findById(id)).thenReturn(Optional.of(atual));
        when(servicoGateway.update(any(Servico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var output = useCase.execute(AtualizarServicoCommand.with(
                UUID.fromString(id.getValue()),
                null,
                null,
                null
        ));

        assertEquals("Alinhamento", output.nome());
        assertEquals("Atual", output.descricao());
        assertEquals(new BigDecimal("100.00"), output.valorReferencia());
    }

    @Test
    void falhaQuandoComandoOuIdNulos() {
        final var ex1 = assertThrows(DomainException.class, () -> useCase.execute(null));
        assertEquals("Comando para atualizar serviço não deve ser nulo", ex1.getMessage());

        final var ex2 = assertThrows(
                DomainException.class,
                () -> useCase.execute(AtualizarServicoCommand.with(null, "Nome", "Desc", BigDecimal.ONE))
        );
        assertEquals("Serviço é obrigatório para atualização", ex2.getMessage());
    }

    @Test
    void falhaQuandoServicoNaoExiste() {
        final var id = ServicoID.unique();
        when(servicoGateway.findById(id)).thenReturn(Optional.empty());

        final var exception = assertThrows(
                DomainException.class,
                () -> useCase.execute(AtualizarServicoCommand.with(UUID.fromString(id.getValue()), "Nome", null, null))
        );

        assertEquals("Serviço não encontrado", exception.getMessage());
    }
}
