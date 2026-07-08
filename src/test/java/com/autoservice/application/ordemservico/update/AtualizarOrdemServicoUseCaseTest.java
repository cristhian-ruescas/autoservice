package com.autoservice.application.ordemservico.update;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.valueobject.DataCriacao;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.domain.veiculo.VeiculoGateway;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.domain.veiculo.valueobject.Cor;
import com.autoservice.domain.veiculo.valueobject.Kilometragem;
import com.autoservice.domain.veiculo.valueobject.Placa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class AtualizarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private VeiculoGateway veiculoGateway;

    @InjectMocks
    private AtualizarOrdemServicoUseCase useCase;

    @Test
    void comandoNuloFalha() {
        assertThrows(DomainException.class, () -> useCase.execute(null));
    }

    @Test
    void ordemServicoIdNuloFalha() {
        assertThrows(DomainException.class, () -> useCase.execute(
                AtualizarOrdemServicoCommand.with(null, null, "x", null, null)));
    }

    @Test
    void atualizaMantendoVeiculoERelato() {
        final var osId = OrdemServicoID.unique();
        final var vId = VeiculoID.unique();
        final var os = OrdemServico.with(
                osId,
                vId,
                OrdemServicoStatus.RECEBIDO,
                DataCriacao.from(LocalDate.now()),
                "relato original",
                1,
                2,
                null,
                null);

        when(ordemServicoGateway.findById(osId)).thenReturn(Optional.of(os));
        when(ordemServicoGateway.update(any(OrdemServico.class))).thenAnswer(returnsFirstArg());

        final var out = useCase.execute(AtualizarOrdemServicoCommand.with(
                UUID.fromString(osId.getValue()),
                null,
                "novo relato",
                null,
                null));

        assertEquals("novo relato", out.relato());
        verify(ordemServicoGateway).update(any(OrdemServico.class));
    }

    @Test
    void trocaVeiculoQuandoInformado() {
        final var osId = OrdemServicoID.unique();
        final var vAntigo = VeiculoID.unique();
        final var vNovo = VeiculoID.unique();
        final var os = OrdemServico.with(
                osId,
                vAntigo,
                OrdemServicoStatus.RECEBIDO,
                DataCriacao.from(LocalDate.now()),
                "r");
        final var veiculo = Veiculo.with(
                vNovo,
                PessoaID.unique(),
                TipoVeiculoID.unique(),
                Placa.from("ABC1D23"),
                Cor.from("Preto"),
                Kilometragem.from(1000));

        when(ordemServicoGateway.findById(osId)).thenReturn(Optional.of(os));
        when(veiculoGateway.findById(vNovo)).thenReturn(Optional.of(veiculo));
        when(ordemServicoGateway.update(any(OrdemServico.class))).thenAnswer(returnsFirstArg());

        final var out = useCase.execute(AtualizarOrdemServicoCommand.with(
                UUID.fromString(osId.getValue()),
                UUID.fromString(vNovo.getValue()),
                null,
                null,
                null));

        assertEquals(vNovo.getValue(), out.veiculoId());
    }

    @Test
    void veiculoInformadoNaoExisteFalha() {
        final var osId = OrdemServicoID.unique();
        final var vNovo = VeiculoID.unique();
        final var os = OrdemServico.with(
                osId,
                VeiculoID.unique(),
                OrdemServicoStatus.RECEBIDO,
                DataCriacao.from(LocalDate.now()),
                "r");

        when(ordemServicoGateway.findById(osId)).thenReturn(Optional.of(os));
        when(veiculoGateway.findById(vNovo)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> useCase.execute(AtualizarOrdemServicoCommand.with(
                UUID.fromString(osId.getValue()),
                UUID.fromString(vNovo.getValue()),
                null,
                null,
                null)));
    }

    @Test
    void tempoPrevistoDiasNegativoFalha() {
        final var osId = OrdemServicoID.unique();
        final var os = OrdemServico.with(
                osId,
                VeiculoID.unique(),
                OrdemServicoStatus.RECEBIDO,
                DataCriacao.from(LocalDate.now()),
                "r");

        when(ordemServicoGateway.findById(osId)).thenReturn(Optional.of(os));

        assertThrows(DomainException.class, () -> useCase.execute(AtualizarOrdemServicoCommand.with(
                UUID.fromString(osId.getValue()),
                null,
                null,
                -1,
                null)));
    }

    @Test
    void tempoPrevistoHorasInvalidasFalha() {
        final var osId = OrdemServicoID.unique();
        final var os = OrdemServico.with(
                osId,
                VeiculoID.unique(),
                OrdemServicoStatus.RECEBIDO,
                DataCriacao.from(LocalDate.now()),
                "r");

        when(ordemServicoGateway.findById(osId)).thenReturn(Optional.of(os));

        assertThrows(DomainException.class, () -> useCase.execute(AtualizarOrdemServicoCommand.with(
                UUID.fromString(osId.getValue()),
                null,
                null,
                null,
                24)));
    }
}
