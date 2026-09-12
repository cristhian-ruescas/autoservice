package com.autoservice.application.ordemservico;

import com.autoservice.application.ordemservico.aprovacao.AprovarOrdemServicoCommand;
import com.autoservice.application.ordemservico.aprovacao.AprovarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.aprovacao.ReprovarOrdemServicoCommand;
import com.autoservice.application.ordemservico.aprovacao.ReprovarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.delete.RemoverOrdemServicoCommand;
import com.autoservice.application.ordemservico.delete.RemoverOrdemServicoUseCase;
import com.autoservice.application.ordemservico.entrega.EntregarOrdemServicoCommand;
import com.autoservice.application.ordemservico.entrega.EntregarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.finalizacao.FinalizarOrdemServicoCommand;
import com.autoservice.application.ordemservico.finalizacao.FinalizarOrdemServicoUseCase;
import com.autoservice.domain.events.DomainEventPublisher;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.valueobject.DataCriacao;
import com.autoservice.domain.veiculo.VeiculoID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdemServicoComandosSimplesUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private DomainEventPublisher eventPublisher;

    private AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase;
    private ReprovarOrdemServicoUseCase reprovarOrdemServicoUseCase;
    private FinalizarOrdemServicoUseCase finalizarOrdemServicoUseCase;
    private EntregarOrdemServicoUseCase entregarOrdemServicoUseCase;
    private RemoverOrdemServicoUseCase removerOrdemServicoUseCase;

    @BeforeEach
    void setUp() {
        this.aprovarOrdemServicoUseCase = new AprovarOrdemServicoUseCase(ordemServicoGateway, eventPublisher);
        this.reprovarOrdemServicoUseCase = new ReprovarOrdemServicoUseCase(ordemServicoGateway, eventPublisher);
        this.finalizarOrdemServicoUseCase = new FinalizarOrdemServicoUseCase(ordemServicoGateway, eventPublisher);
        this.entregarOrdemServicoUseCase = new EntregarOrdemServicoUseCase(ordemServicoGateway, eventPublisher);
        this.removerOrdemServicoUseCase = new RemoverOrdemServicoUseCase(ordemServicoGateway, eventPublisher);
    }

    @Test
    void aprovarOrcamento() {
        final var id = OrdemServicoID.unique();
        final var os = OrdemServico.with(
                id,
                VeiculoID.unique(),
                OrdemServicoStatus.AGUARDANDO_APROVACAO,
                DataCriacao.from(LocalDate.now()),
                "r");

        when(ordemServicoGateway.findById(id)).thenReturn(Optional.of(os));
        when(ordemServicoGateway.update(any(OrdemServico.class))).thenAnswer(returnsFirstArg());

        final var out = aprovarOrdemServicoUseCase.execute(AprovarOrdemServicoCommand.with(UUID.fromString(id.getValue())));

        assertEquals(OrdemServicoStatus.EM_EXECUCAO.name(), out.status());
        verify(eventPublisher, times(2)).publishEvent(any());
    }

    @Test
    void reprovarOrcamento() {
        final var id = OrdemServicoID.unique();
        final var os = OrdemServico.with(
                id,
                VeiculoID.unique(),
                OrdemServicoStatus.AGUARDANDO_APROVACAO,
                DataCriacao.from(LocalDate.now()),
                "r");

        when(ordemServicoGateway.findById(id)).thenReturn(Optional.of(os));
        when(ordemServicoGateway.update(any(OrdemServico.class))).thenAnswer(returnsFirstArg());

        final var out = reprovarOrdemServicoUseCase.execute(ReprovarOrdemServicoCommand.with(UUID.fromString(id.getValue())));

        assertEquals(OrdemServicoStatus.REPROVADO.name(), out.status());
    }

    @Test
    void finalizarExecucao() {
        final var id = OrdemServicoID.unique();
        final var os = OrdemServico.with(
                id,
                VeiculoID.unique(),
                OrdemServicoStatus.EM_EXECUCAO,
                DataCriacao.from(LocalDate.now()),
                "r");

        when(ordemServicoGateway.findById(id)).thenReturn(Optional.of(os));
        when(ordemServicoGateway.update(any(OrdemServico.class))).thenAnswer(returnsFirstArg());

        final var out = finalizarOrdemServicoUseCase.execute(FinalizarOrdemServicoCommand.with(UUID.fromString(id.getValue())));

        assertEquals(OrdemServicoStatus.FINALIZADA.name(), out.status());
        verify(eventPublisher, times(2)).publishEvent(any());
    }

    @Test
    void entregarFinalizada() {
        final var id = OrdemServicoID.unique();
        final var os = OrdemServico.with(
                id,
                VeiculoID.unique(),
                OrdemServicoStatus.FINALIZADA,
                DataCriacao.from(LocalDate.now()),
                "r");

        when(ordemServicoGateway.findById(id)).thenReturn(Optional.of(os));
        when(ordemServicoGateway.update(any(OrdemServico.class))).thenAnswer(returnsFirstArg());

        final var out = entregarOrdemServicoUseCase.execute(EntregarOrdemServicoCommand.with(UUID.fromString(id.getValue())));

        assertEquals(OrdemServicoStatus.ENTREGUE.name(), out.status());
    }

    @Test
    void removerOrdemCancela() {
        final var id = OrdemServicoID.unique();
        final var os = OrdemServico.with(
                id,
                VeiculoID.unique(),
                OrdemServicoStatus.RECEBIDO,
                DataCriacao.from(LocalDate.now()),
                "r");

        when(ordemServicoGateway.findById(id)).thenReturn(Optional.of(os));
        when(ordemServicoGateway.update(any(OrdemServico.class))).thenAnswer(returnsFirstArg());

        final var out = removerOrdemServicoUseCase.execute(RemoverOrdemServicoCommand.with(UUID.fromString(id.getValue())));

        assertEquals(OrdemServicoStatus.CANCELADO.name(), out.status());
    }

    @Test
    void removerOrdemComandoNuloFalha() {
        assertThrows(DomainException.class, () -> removerOrdemServicoUseCase.execute(null));
    }
}
