package com.autoservice.application.ordemservico.itemservico;

import com.autoservice.domain.estoque.EstoqueID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.ItemServico;
import com.autoservice.domain.itemservico.ItemServicoGateway;
import com.autoservice.domain.itemservico.ItemServicoID;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarItemServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private ItemServicoGateway itemServicoGateway;

    @Mock
    private PecaGateway pecaGateway;

    @InjectMocks
    private AtualizarItemServicoUseCase useCase;

    @Test
    void atualizaItemServico() {
        final var osId = OrdemServicoID.unique();
        final var os = OrdemServico.with(
                osId,
                VeiculoID.unique(),
                OrdemServicoStatus.EM_DIAGNOSTICO,
                DataCriacao.from(LocalDate.now()),
                "r");
        final var itemId = ItemServicoID.unique();
        final var item = ItemServico.with(
                itemId,
                osId,
                ItemServicoTipo.SERVICO,
                "Alinhamento",
                null,
                1,
                new BigDecimal("50.00"));

        when(ordemServicoGateway.findById(osId)).thenReturn(Optional.of(os));
        when(itemServicoGateway.findById(itemId)).thenReturn(Optional.of(item));
        when(itemServicoGateway.update(any(ItemServico.class))).thenAnswer(returnsFirstArg());

        final var out = useCase.execute(AtualizarItemServicoCommand.with(
                UUID.fromString(osId.getValue()),
                UUID.fromString(itemId.getValue()),
                ItemServicoTipo.SERVICO,
                "Balanceamento",
                null,
                null,
                new BigDecimal("120.00")));

        assertEquals("Balanceamento", out.descricao());
        verify(itemServicoGateway).update(any(ItemServico.class));
    }

    @Test
    void atualizaItemPecaComNovaPecaId() {
        final var osId = OrdemServicoID.unique();
        final var os = OrdemServico.with(
                osId,
                VeiculoID.unique(),
                OrdemServicoStatus.EM_DIAGNOSTICO,
                DataCriacao.from(LocalDate.now()),
                "r");
        final var itemId = ItemServicoID.unique();
        final var pecaAntiga = PecaID.unique();
        final var pecaNova = PecaID.unique();
        final var item = ItemServico.with(
                itemId,
                osId,
                ItemServicoTipo.PECA,
                "Filtro",
                pecaAntiga,
                1,
                new BigDecimal("10.00"));
        final var peca = Peca.with(
                pecaNova,
                "Filtro novo",
                "F-2",
                "Mann",
                new BigDecimal("99.00"),
                EstoqueID.unique(),
                TipoVeiculoID.unique());

        when(ordemServicoGateway.findById(osId)).thenReturn(Optional.of(os));
        when(itemServicoGateway.findById(itemId)).thenReturn(Optional.of(item));
        when(pecaGateway.findById(pecaNova)).thenReturn(Optional.of(peca));
        when(itemServicoGateway.update(any(ItemServico.class))).thenAnswer(returnsFirstArg());

        final var out = useCase.execute(AtualizarItemServicoCommand.with(
                UUID.fromString(osId.getValue()),
                UUID.fromString(itemId.getValue()),
                ItemServicoTipo.PECA,
                "descr",
                UUID.fromString(pecaNova.getValue()),
                2,
                null));

        assertEquals(2, out.quantidade());
        assertEquals("descr", out.descricao());
    }

    @Test
    void statusOrdemDiferenteDeDiagnosticoFalha() {
        final var osId = OrdemServicoID.unique();
        final var os = OrdemServico.with(
                osId,
                VeiculoID.unique(),
                OrdemServicoStatus.RECEBIDO,
                DataCriacao.from(LocalDate.now()),
                "r");
        final var itemId = ItemServicoID.unique();

        when(ordemServicoGateway.findById(osId)).thenReturn(Optional.of(os));

        assertThrows(DomainException.class, () -> useCase.execute(AtualizarItemServicoCommand.with(
                UUID.fromString(osId.getValue()),
                UUID.fromString(itemId.getValue()),
                ItemServicoTipo.SERVICO,
                "y",
                null,
                null,
                BigDecimal.TEN)));
    }

    @Test
    void itemNaoPertenceOrdemFalha() {
        final var osId = OrdemServicoID.unique();
        final var outroOs = OrdemServicoID.unique();
        final var os = OrdemServico.with(
                osId,
                VeiculoID.unique(),
                OrdemServicoStatus.EM_DIAGNOSTICO,
                DataCriacao.from(LocalDate.now()),
                "r");
        final var itemId = ItemServicoID.unique();
        final var item = ItemServico.with(
                itemId,
                outroOs,
                ItemServicoTipo.SERVICO,
                "x",
                null,
                1,
                BigDecimal.ONE);

        when(ordemServicoGateway.findById(osId)).thenReturn(Optional.of(os));
        when(itemServicoGateway.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(DomainException.class, () -> useCase.execute(AtualizarItemServicoCommand.with(
                UUID.fromString(osId.getValue()),
                UUID.fromString(itemId.getValue()),
                ItemServicoTipo.SERVICO,
                "y",
                null,
                null,
                BigDecimal.TEN)));
    }
}
