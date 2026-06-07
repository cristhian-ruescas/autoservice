package com.autoservice.application.atendimento.create;

import com.autoservice.application.tipoveiculo.TipoVeiculoResolver;
import com.autoservice.domain.atendimento.events.*;
import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.events.DomainEventPublisher;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.veiculo.Veiculo;

import java.util.Objects;

import org.springframework.stereotype.Component;

@Component
public final class AtendimentoDomainEventPublisher {

    private final DomainEventPublisher eventPublisher;
    private final PessoaAtendimentoOrchestrator pessoaAtendimentoOrchestrator;

    public AtendimentoDomainEventPublisher(
            final DomainEventPublisher eventPublisher,
            final PessoaAtendimentoOrchestrator pessoaAtendimentoOrchestrator
    ) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.pessoaAtendimentoOrchestrator = Objects.requireNonNull(pessoaAtendimentoOrchestrator);
    }

    public void publicarPessoa(final PessoaAtendimentoOrchestrator.PessoaResultado pessoa) {
        if (pessoa.representanteLegalCriado()) {
            this.eventPublisher.publishEvent(new RepresentanteLegalAtendimentoCriadoEvent(
                    pessoa.representanteLegal().getId()
            ));
        }

        if (pessoa.criada()) {
            this.eventPublisher.publishEvent(new PessoaAtendimentoCriadaEvent(
                    pessoa.pessoa().getId(),
                    this.pessoaAtendimentoOrchestrator.tipoPessoaCodigo(pessoa.pessoa()),
                    pessoa.representanteLegal() == null ? null : pessoa.representanteLegal().getId()
            ));
        }
    }

    public void publicarClienteCriado(final Cliente cliente) {
        this.eventPublisher.publishEvent(new ClienteAtendimentoCriadoEvent(
                cliente.getId(),
                cliente.getPessoaId()
        ));
    }

    public void publicarTipoVeiculoCriado(final TipoVeiculoResolver.Resultado tipoVeiculo) {
        if (tipoVeiculo.criado()) {
            this.eventPublisher.publishEvent(new TipoVeiculoAtendimentoCriadoEvent(
                    tipoVeiculo.tipoVeiculo().getId()
            ));
        }
    }

    public void publicarVeiculoCriado(final Veiculo veiculo) {
        this.eventPublisher.publishEvent(new VeiculoAtendimentoCriadoEvent(
                veiculo.getId(),
                veiculo.getProprietarioId(),
                veiculo.getTipoVeiculoId()
        ));
    }

    public void publicarOrdemServicoAberta(final OrdemServico ordemServico) {
        this.eventPublisher.publishEvent(new OrdemServicoAtendimentoAbertaEvent(
                ordemServico.getId(),
                ordemServico.getVeiculoId()
        ));
    }
}
