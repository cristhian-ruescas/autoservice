package com.autoservice.infrastructure.ordemservico.events;

import com.autoservice.application.ordemservico.detail.DetailOrdemServicoQuery;
import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusClienteNotifier;
import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusNotificacao;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.events.OrdemServicoStatusAlteradoEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;
import java.util.UUID;

@Component
public class NotificarClienteStatusOrdemServicoListener {

    private final DetailOrdemServicoQuery detailOrdemServicoQuery;
    private final OrdemServicoStatusClienteNotifier ordemServicoStatusClienteNotifier;
    private final String appBaseUrl;

    public NotificarClienteStatusOrdemServicoListener(
            final DetailOrdemServicoQuery detailOrdemServicoQuery,
            final OrdemServicoStatusClienteNotifier ordemServicoStatusClienteNotifier,
            @Value("${autoservice.app.base-url}") final String appBaseUrl
    ) {
        this.detailOrdemServicoQuery = Objects.requireNonNull(detailOrdemServicoQuery);
        this.ordemServicoStatusClienteNotifier = Objects.requireNonNull(ordemServicoStatusClienteNotifier);
        this.appBaseUrl = appBaseUrl;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(final OrdemServicoStatusAlteradoEvent event) {
        final var ordemServico = this.detailOrdemServicoQuery.execute(
                UUID.fromString(event.getOrdemServicoId().getValue())
        );

        this.ordemServicoStatusClienteNotifier.notificar(new OrdemServicoStatusNotificacao(
                ordemServico.ordemServicoId(),
                nomeStatus(event.getStatusAnterior()),
                event.getStatusNovo().name(),
                event.getStatusNovo().getDescricao(),
                ordemServico.cliente() == null ? null : ordemServico.cliente().email(),
                nomeCliente(ordemServico.cliente()),
                ordemServico.veiculo() == null ? null : ordemServico.veiculo().placa(),
                andamentoUrl(ordemServico.ordemServicoId()),
                event.occurredOn()
        ));
    }

    private String nomeStatus(final OrdemServicoStatus status) {
        return status == null ? null : status.name();
    }

    private String nomeCliente(final com.autoservice.application.ordemservico.list.ListOrdemServicoOutput.ClienteOutput cliente) {
        if (cliente == null) {
            return null;
        }

        if (cliente.nome() != null && !cliente.nome().isBlank()) {
            return cliente.nome();
        }

        return cliente.razaoSocial();
    }

    private String andamentoUrl(final String ordemServicoId) {
        return normalizedAppBaseUrl() + "/ordens-servico/" + ordemServicoId + "/andamento";
    }

    private String normalizedAppBaseUrl() {
        if (this.appBaseUrl.endsWith("/")) {
            return this.appBaseUrl.substring(0, this.appBaseUrl.length() - 1);
        }

        return this.appBaseUrl;
    }
}
