package com.autoservice.application.ordemservico.itemservico;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.ItemServico;
import com.autoservice.domain.itemservico.ItemServicoGateway;
import com.autoservice.domain.itemservico.enums.ItemServicoTipo;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.valueobject.DataCriacao;
import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.peca.PecaGateway;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.VeiculoID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdicionarItensServicoUseCase")
class AdicionarItensServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private ItemServicoGateway itemServicoGateway;

    @Mock
    private PecaGateway pecaGateway;

    private ItemServicoOrchestrator itemServicoOrchestrator;
    private AdicionarItensServicoUseCase useCase;

    @BeforeEach
    void setUp() {
        itemServicoOrchestrator = new ItemServicoOrchestrator(ordemServicoGateway, pecaGateway);
        useCase = new AdicionarItensServicoUseCase(itemServicoOrchestrator, itemServicoGateway);
    }

    @Test
    void listaVaziaFalha() {
        final OrdemServicoID id = OrdemServicoID.unique();
        final var cmd = AdicionarItensServicoCommand.with(UUID.fromString(id.getValue()), List.of());
        assertThrows(DomainException.class, () -> useCase.execute(cmd));
    }

    @Test
    void ordemNaoEncontrada() {
        final OrdemServicoID id = OrdemServicoID.unique();
        when(ordemServicoGateway.findById(id)).thenReturn(Optional.empty());
        final var cmd = AdicionarItensServicoCommand.with(
                UUID.fromString(id.getValue()),
                List.of(AdicionarItemServicoCommand.with(
                        UUID.fromString(id.getValue()),
                        ItemServicoTipo.SERVICO,
                        "x",
                        null,
                        1,
                        BigDecimal.ONE)));
        assertThrows(DomainException.class, () -> useCase.execute(cmd));
    }

    @Test
    void statusIncorretoFalha() {
        final OrdemServicoID id = OrdemServicoID.unique();
        final OrdemServico os = OrdemServico.with(
                id,
                VeiculoID.unique(),
                OrdemServicoStatus.RECEBIDO,
                DataCriacao.from(LocalDate.now()),
                "r");
        when(ordemServicoGateway.findById(id)).thenReturn(Optional.of(os));
        final var cmd = AdicionarItensServicoCommand.with(
                UUID.fromString(id.getValue()),
                List.of(AdicionarItemServicoCommand.with(
                        UUID.fromString(id.getValue()),
                        ItemServicoTipo.SERVICO,
                        "x",
                        null,
                        1,
                        BigDecimal.ONE)));
        assertThrows(DomainException.class, () -> useCase.execute(cmd));
    }

    @Test
    void pecaSemIdFalha() {
        final OrdemServicoID id = OrdemServicoID.unique();
        final OrdemServico os = OrdemServico.with(
                id,
                VeiculoID.unique(),
                OrdemServicoStatus.EM_DIAGNOSTICO,
                DataCriacao.from(LocalDate.now()),
                "r");
        when(ordemServicoGateway.findById(id)).thenReturn(Optional.of(os));
        final var cmd = AdicionarItensServicoCommand.with(
                UUID.fromString(id.getValue()),
                List.of(AdicionarItemServicoCommand.with(
                        UUID.fromString(id.getValue()),
                        ItemServicoTipo.PECA,
                        "x",
                        null,
                        1,
                        BigDecimal.ONE)));
        assertThrows(DomainException.class, () -> useCase.execute(cmd));
    }

    @Test
    void pecaNaoEncontrada() {
        final OrdemServicoID id = OrdemServicoID.unique();
        final OrdemServico os = OrdemServico.with(
                id,
                VeiculoID.unique(),
                OrdemServicoStatus.EM_DIAGNOSTICO,
                DataCriacao.from(LocalDate.now()),
                "r");
        when(ordemServicoGateway.findById(id)).thenReturn(Optional.of(os));
        final PecaID pecaId = PecaID.unique();
        when(pecaGateway.findById(pecaId)).thenReturn(Optional.empty());
        final var cmd = AdicionarItensServicoCommand.with(
                UUID.fromString(id.getValue()),
                List.of(AdicionarItemServicoCommand.with(
                        UUID.fromString(id.getValue()),
                        ItemServicoTipo.PECA,
                        "x",
                        UUID.fromString(pecaId.getValue()),
                        1,
                        BigDecimal.ONE)));
        assertThrows(DomainException.class, () -> useCase.execute(cmd));
    }

    @Test
    void adicionaServico() {
        final OrdemServicoID id = OrdemServicoID.unique();
        final OrdemServico os = OrdemServico.with(
                id,
                VeiculoID.unique(),
                OrdemServicoStatus.EM_DIAGNOSTICO,
                DataCriacao.from(LocalDate.now()),
                "r");
        when(ordemServicoGateway.findById(id)).thenReturn(Optional.of(os));
        when(itemServicoGateway.create(any(ItemServico.class))).thenAnswer(returnsFirstArg());

        final var cmd = AdicionarItensServicoCommand.with(
                UUID.fromString(id.getValue()),
                List.of(AdicionarItemServicoCommand.with(
                        UUID.fromString(id.getValue()),
                        ItemServicoTipo.SERVICO,
                        "Alinhamento",
                        null,
                        1,
                        new BigDecimal("80.00"))));

        final AdicionarItensServicoOutput out = useCase.execute(cmd);

        assertEquals(1, out.itens().size());
        assertEquals(0, out.valorTotal().compareTo(new BigDecimal("80.00")));
        verify(itemServicoGateway).create(any(ItemServico.class));
    }

    @Test
    void adicionaPecaUsaDescricaoDaPecaQuandoEmBranco() {
        final OrdemServicoID id = OrdemServicoID.unique();
        final OrdemServico os = OrdemServico.with(
                id,
                VeiculoID.unique(),
                OrdemServicoStatus.EM_DIAGNOSTICO,
                DataCriacao.from(LocalDate.now()),
                "r");
        when(ordemServicoGateway.findById(id)).thenReturn(Optional.of(os));
        final PecaID pecaId = PecaID.unique();
        final Peca peca = Peca.with(
                pecaId,
                "Filtro original",
                "F-1",
                "Mann",
                new BigDecimal("25.00"),
                com.autoservice.domain.estoque.EstoqueID.unique(),
                TipoVeiculoID.unique());
        when(pecaGateway.findById(pecaId)).thenReturn(Optional.of(peca));
        when(itemServicoGateway.create(any(ItemServico.class))).thenAnswer(returnsFirstArg());

        final var cmd = AdicionarItensServicoCommand.with(
                UUID.fromString(id.getValue()),
                List.of(AdicionarItemServicoCommand.with(
                        UUID.fromString(id.getValue()),
                        ItemServicoTipo.PECA,
                        "  ",
                        UUID.fromString(pecaId.getValue()),
                        2,
                        new BigDecimal("99.00"))));

        final AdicionarItensServicoOutput out = useCase.execute(cmd);

        assertEquals("Filtro original", out.itens().getFirst().descricao());
        verify(itemServicoGateway).create(any(ItemServico.class));
    }
}
