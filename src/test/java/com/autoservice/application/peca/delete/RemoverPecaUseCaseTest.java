package com.autoservice.application.peca.delete;

import com.autoservice.domain.estoque.Estoque;
import com.autoservice.domain.estoque.EstoqueGateway;
import com.autoservice.domain.estoque.EstoqueID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.ItemServicoGateway;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.peca.PecaGateway;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverPecaUseCaseTest {

    @Mock
    private PecaGateway pecaGateway;

    @Mock
    private EstoqueGateway estoqueGateway;

    @Mock
    private ItemServicoGateway itemServicoGateway;

    @InjectMocks
    private RemoverPecaUseCase useCase;

    @Test
    void removeQuandoSemSaldoEVinculos() {
        final var pid = PecaID.unique();
        final var eid = EstoqueID.unique();
        final var peca = Peca.with(
                pid,
                "P",
                "C",
                "M",
                BigDecimal.ONE,
                eid,
                TipoVeiculoID.unique());
        final var estoque = Estoque.with(eid, 0, 1, "A1");

        when(pecaGateway.findById(pid)).thenReturn(Optional.of(peca));
        when(estoqueGateway.findById(eid)).thenReturn(Optional.of(estoque));
        when(itemServicoGateway.existsByPecaIdAndOrdemServicoStatusNot(pid, OrdemServicoStatus.ENTREGUE))
                .thenReturn(false);

        useCase.execute(RemoverPecaCommand.with(UUID.fromString(pid.getValue())));

        verify(pecaGateway).deleteById(pid);
    }

    @Test
    void naoRemoveComSaldoPositivo() {
        final var pid = PecaID.unique();
        final var eid = EstoqueID.unique();
        final var peca = Peca.with(
                pid,
                "P",
                "C",
                "M",
                BigDecimal.ONE,
                eid,
                TipoVeiculoID.unique());
        final var estoque = Estoque.with(eid, 5, 1, "A1");

        when(pecaGateway.findById(pid)).thenReturn(Optional.of(peca));
        when(estoqueGateway.findById(eid)).thenReturn(Optional.of(estoque));

        assertThrows(DomainException.class, () -> useCase.execute(RemoverPecaCommand.with(UUID.fromString(pid.getValue()))));
    }

    @Test
    void comandoNuloFalha() {
        assertThrows(DomainException.class, () -> useCase.execute(null));
    }
}
