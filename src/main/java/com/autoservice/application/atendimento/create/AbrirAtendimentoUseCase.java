package com.autoservice.application.atendimento.create;

import com.autoservice.application.UseCase;
import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteGateway;
import com.autoservice.domain.events.DomainEventPublisher;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.pessoa.Pessoa;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.domain.veiculo.VeiculoGateway;
import com.autoservice.domain.veiculo.valueobject.Cor;
import com.autoservice.domain.veiculo.valueobject.Kilometragem;
import com.autoservice.domain.veiculo.valueobject.Placa;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AbrirAtendimentoUseCase extends UseCase<AbrirAtendimentoCommand, AbrirAtendimentoOutput> {

    private final PessoaAtendimentoOrchestrator pessoaAtendimentoOrchestrator;
    private final TipoVeiculoAtendimentoResolver tipoVeiculoResolver;
    private final AtendimentoDomainEventPublisher atendimentoEvents;
    private final DomainEventPublisher eventPublisher;
    private final ClienteGateway clienteGateway;
    private final VeiculoGateway veiculoGateway;
    private final OrdemServicoGateway ordemServicoGateway;

    public AbrirAtendimentoUseCase(
            final PessoaAtendimentoOrchestrator pessoaAtendimentoOrchestrator,
            final TipoVeiculoAtendimentoResolver tipoVeiculoResolver,
            final AtendimentoDomainEventPublisher atendimentoEvents,
            final ClienteGateway clienteGateway,
            final VeiculoGateway veiculoGateway,
            final OrdemServicoGateway ordemServicoGateway,
            final DomainEventPublisher eventPublisher
    ) {
        this.pessoaAtendimentoOrchestrator = Objects.requireNonNull(pessoaAtendimentoOrchestrator);
        this.tipoVeiculoResolver = Objects.requireNonNull(tipoVeiculoResolver);
        this.atendimentoEvents = Objects.requireNonNull(atendimentoEvents);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.clienteGateway = Objects.requireNonNull(clienteGateway);
        this.veiculoGateway = Objects.requireNonNull(veiculoGateway);
        this.ordemServicoGateway = Objects.requireNonNull(ordemServicoGateway);
    }

    @Override
    @Transactional
    public AbrirAtendimentoOutput execute(final AbrirAtendimentoCommand command) {
        final var pessoa = this.pessoaAtendimentoOrchestrator.obterOuCriar(command);
        this.atendimentoEvents.publicarPessoa(pessoa);

        final var cliente = criarCliente(pessoa.pessoa());
        this.atendimentoEvents.publicarClienteCriado(cliente);

        final var tipoVeiculo = this.tipoVeiculoResolver.obterOuCriar(command);
        this.atendimentoEvents.publicarTipoVeiculoCriado(tipoVeiculo);

        final var veiculo = criarVeiculo(command, pessoa.pessoa(), tipoVeiculo.tipoVeiculo());
        this.atendimentoEvents.publicarVeiculoCriado(veiculo);

        final var ordemServico = abrirOrdemServico(command, veiculo);
        this.atendimentoEvents.publicarOrdemServicoAberta(ordemServico);

        return AbrirAtendimentoOutput.from(pessoa.pessoa(), cliente, veiculo, ordemServico);
    }

    private Cliente criarCliente(final Pessoa pessoa) {
        return this.clienteGateway.create(Cliente.newCliente(pessoa.getId()));
    }

    private Veiculo criarVeiculo(
            final AbrirAtendimentoCommand command,
            final Pessoa pessoa,
            final TipoVeiculo tipoVeiculo
    ) {
        return this.veiculoGateway.create(Veiculo.newVeiculo(
                pessoa.getId(),
                tipoVeiculo.getId(),
                Placa.from(command.placa()),
                Cor.from(command.cor()),
                Kilometragem.from(command.kilometragem())
        ));
    }

    private OrdemServico abrirOrdemServico(
            final AbrirAtendimentoCommand command,
            final Veiculo veiculo
    ) {
        final var ordemServicoCriada = this.ordemServicoGateway.create(
                OrdemServico.newOrdemServico(veiculo.getId(), command.relato())
        );

        ordemServicoCriada.getDomainEvents().forEach(this.eventPublisher::publishEvent);
        ordemServicoCriada.clearEvents();

        return ordemServicoCriada;
    }
}
