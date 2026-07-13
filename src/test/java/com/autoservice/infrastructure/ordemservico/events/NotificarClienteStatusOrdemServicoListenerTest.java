package com.autoservice.infrastructure.ordemservico.events;

import com.autoservice.application.ordemservico.detail.DetailOrdemServicoQuery;
import com.autoservice.application.ordemservico.detail.DetailOrdemServicoOutput;
import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusClienteNotifier;
import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusNotificacao;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.events.OrdemServicoStatusAlteradoEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.autoservice.support.NotificacaoTestFixtures.DATA_REFERENCIA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificarClienteStatusOrdemServicoListenerTest {

    @Mock
    private DetailOrdemServicoQuery detailOrdemServicoQuery;

    @Mock
    private OrdemServicoStatusClienteNotifier ordemServicoStatusClienteNotifier;

    @Test
    void deveNotificarClienteComDadosDaOrdem() {
        final var listener = new NotificarClienteStatusOrdemServicoListener(
                detailOrdemServicoQuery,
                ordemServicoStatusClienteNotifier,
                "http://localhost:8088/"
        );
        final var ordemServicoId = OrdemServicoID.unique();
        final var event = new OrdemServicoStatusAlteradoEvent(
                ordemServicoId,
                OrdemServicoStatus.RECEBIDO,
                OrdemServicoStatus.EM_DIAGNOSTICO
        );

        when(detailOrdemServicoQuery.execute(any(UUID.class))).thenReturn(detalhe(ordemServicoId.getValue()));

        listener.on(event);

        final ArgumentCaptor<OrdemServicoStatusNotificacao> captor =
                ArgumentCaptor.forClass(OrdemServicoStatusNotificacao.class);
        verify(ordemServicoStatusClienteNotifier).notificar(captor.capture());

        final var notificacao = captor.getValue();
        assertEquals(ordemServicoId.getValue(), notificacao.ordemServicoId());
        assertEquals("RECEBIDO", notificacao.statusAnterior());
        assertEquals("EM_DIAGNOSTICO", notificacao.statusNovo());
        assertEquals("Maria", notificacao.clienteNome());
        assertEquals(
                "http://localhost:8088/ordens-servico/" + ordemServicoId.getValue() + "/andamento",
                notificacao.andamentoUrl()
        );
    }

    @Test
    void deveUsarRazaoSocialQuandoNomeNaoInformado() {
        final var listener = new NotificarClienteStatusOrdemServicoListener(
                detailOrdemServicoQuery,
                ordemServicoStatusClienteNotifier,
                "http://localhost:8088"
        );
        final var ordemServicoId = OrdemServicoID.unique();
        final var event = new OrdemServicoStatusAlteradoEvent(
                ordemServicoId,
                null,
                OrdemServicoStatus.RECEBIDO
        );
        final var cliente = new ListOrdemServicoOutput.ClienteOutput(
                "c1",
                "JURIDICA",
                null,
                null,
                "Empresa Teste",
                "11222333000181",
                "contato@empresa.com",
                "11999999999",
                null
        );
        final var detalhe = new DetailOrdemServicoOutput(
                ordemServicoId.getValue(),
                "RECEBIDO",
                DATA_REFERENCIA,
                "relato",
                null,
                null,
                null,
                null,
                null,
                cliente,
                null,
                null
        );

        when(detailOrdemServicoQuery.execute(any(UUID.class))).thenReturn(detalhe);

        listener.on(event);

        final ArgumentCaptor<OrdemServicoStatusNotificacao> captor =
                ArgumentCaptor.forClass(OrdemServicoStatusNotificacao.class);
        verify(ordemServicoStatusClienteNotifier).notificar(captor.capture());
        assertEquals("Empresa Teste", captor.getValue().clienteNome());
    }

    private static DetailOrdemServicoOutput detalhe(final String ordemServicoId) {
        final var veiculo = new ListOrdemServicoOutput.VeiculoOutput(
                "v1", "ABC1D23", "Fiat", "Uno", 2015, "Branca", 50000
        );
        final var cliente = new ListOrdemServicoOutput.ClienteOutput(
                "c1", "FISICA", "Maria", "11111111111", null, null, "m@e.com", "11999999999", null
        );
        final var list = new ListOrdemServicoOutput(
                ordemServicoId,
                "EM_DIAGNOSTICO",
                DATA_REFERENCIA,
                "Relato",
                null,
                null,
                null,
                null,
                veiculo,
                cliente
        );

        return DetailOrdemServicoOutput.from(list, java.util.List.of());
    }
}
