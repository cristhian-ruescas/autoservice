package com.autoservice.application.ordemservico.itemservico;

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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverItemServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private ItemServicoGateway itemServicoGateway;

    @InjectMocks
    private RemoverItemServicoUseCase useCase;

    @Test
    void removeItemEmDiagnostico() {
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
                "Serviço",
                null,
                1,
                BigDecimal.TEN);

        when(ordemServicoGateway.findById(osId)).thenReturn(Optional.of(os));
        when(itemServicoGateway.findById(itemId)).thenReturn(Optional.of(item));

        useCase.execute(RemoverItemServicoCommand.with(
                UUID.fromString(osId.getValue()),
                UUID.fromString(itemId.getValue())));

        verify(itemServicoGateway).deleteById(itemId);
    }

    @Test
    void ordemForaDiagnosticoFalha() {
        final var osId = OrdemServicoID.unique();
        final var os = OrdemServico.with(
                osId,
                VeiculoID.unique(),
                OrdemServicoStatus.RECEBIDO,
                DataCriacao.from(LocalDate.now()),
                "r");

        when(ordemServicoGateway.findById(osId)).thenReturn(Optional.of(os));

        assertThrows(DomainException.class, () -> useCase.execute(RemoverItemServicoCommand.with(
                UUID.fromString(osId.getValue()),
                UUID.randomUUID())));
    }
}
