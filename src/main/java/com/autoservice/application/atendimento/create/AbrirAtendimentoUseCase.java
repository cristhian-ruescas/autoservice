package com.autoservice.application.atendimento.create;

import com.autoservice.application.UseCase;
import com.autoservice.application.atendimento.create.enums.TipoPessoaAtendimento;
import com.autoservice.domain.atendimento.events.ClienteAtendimentoCriadoEvent;
import com.autoservice.domain.atendimento.events.OrdemServicoAtendimentoAbertaEvent;
import com.autoservice.domain.atendimento.events.PessoaAtendimentoCriadaEvent;
import com.autoservice.domain.atendimento.events.RepresentanteLegalAtendimentoCriadoEvent;
import com.autoservice.domain.atendimento.events.TipoVeiculoAtendimentoCriadoEvent;
import com.autoservice.domain.atendimento.events.VeiculoAtendimentoCriadoEvent;
import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteGateway;
import com.autoservice.domain.events.DomainEventPublisher;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.pessoa.Pessoa;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaGateway;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.domain.pessoa.valueobject.CNPJ;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.tipoveiculo.TipoVeiculoGateway;
import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.domain.veiculo.VeiculoGateway;
import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.domain.veiculo.valueobject.Cor;
import com.autoservice.domain.veiculo.valueobject.Kilometragem;
import com.autoservice.domain.veiculo.valueobject.Marca;
import com.autoservice.domain.veiculo.valueobject.Modelo;
import com.autoservice.domain.veiculo.valueobject.Placa;
import com.autoservice.validation.Error;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AbrirAtendimentoUseCase extends UseCase<AbrirAtendimentoCommand, AbrirAtendimentoOutput> {

    private final PessoaGateway pessoaGateway;
    private final ClienteGateway clienteGateway;
    private final VeiculoGateway veiculoGateway;
    private final TipoVeiculoGateway tipoVeiculoGateway;
    private final OrdemServicoGateway ordemServicoGateway;
    private final DomainEventPublisher eventPublisher;

    public AbrirAtendimentoUseCase(
            final PessoaGateway pessoaGateway,
            final ClienteGateway clienteGateway,
            final VeiculoGateway veiculoGateway,
            final TipoVeiculoGateway tipoVeiculoGateway,
            final OrdemServicoGateway ordemServicoGateway,
            final DomainEventPublisher eventPublisher
    ) {
        this.pessoaGateway = Objects.requireNonNull(pessoaGateway);
        this.clienteGateway = Objects.requireNonNull(clienteGateway);
        this.veiculoGateway = Objects.requireNonNull(veiculoGateway);
        this.tipoVeiculoGateway = Objects.requireNonNull(tipoVeiculoGateway);
        this.ordemServicoGateway = Objects.requireNonNull(ordemServicoGateway);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    @Transactional
    public AbrirAtendimentoOutput execute(final AbrirAtendimentoCommand command) {
        final var pessoa = obterOuCriarPessoa(command);
        publicarEventosPessoa(pessoa);

        final var clienteCriado = criarCliente(pessoa.pessoa());
        publicarClienteCriado(clienteCriado);

        final var tipoVeiculo = obterOuCriarTipoVeiculo(command);
        publicarTipoVeiculoCriado(tipoVeiculo);

        final var veiculoCriado = criarVeiculo(command, pessoa.pessoa(), tipoVeiculo.tipoVeiculo());
        publicarVeiculoCriado(veiculoCriado);

        final var ordemServicoCriada = abrirOrdemServico(command, veiculoCriado);
        publicarOrdemServicoAberta(ordemServicoCriada);

        return AbrirAtendimentoOutput.from(
                pessoa.pessoa(),
                clienteCriado,
                veiculoCriado,
                ordemServicoCriada
        );
    }

    private Cliente criarCliente(final Pessoa pessoa) {
        final var cliente = Cliente.newCliente(pessoa.getId());

        return this.clienteGateway.create(cliente);
    }

    private TipoVeiculoResultado obterOuCriarTipoVeiculo(final AbrirAtendimentoCommand command) {
        return this.tipoVeiculoGateway.findByMarcaModeloAno(command.marca(), command.modelo(), command.ano())
                .map(tipoVeiculo -> new TipoVeiculoResultado(tipoVeiculo, false))
                .orElseGet(() -> new TipoVeiculoResultado(this.tipoVeiculoGateway.create(TipoVeiculo.newTipoVeiculo(
                        Marca.from(command.marca()),
                        Modelo.from(command.modelo()),
                        Ano.from(command.ano())
                )), true));
    }

    private Veiculo criarVeiculo(
            final AbrirAtendimentoCommand command,
            final Pessoa pessoa,
            final TipoVeiculo tipoVeiculo
    ) {
        final var veiculo = Veiculo.newVeiculo(
                pessoa.getId(),
                tipoVeiculo.getId(),
                Placa.from(command.placa()),
                Cor.from(command.cor()),
                Kilometragem.from(command.kilometragem())
        );

        return this.veiculoGateway.create(veiculo);
    }

    private OrdemServico abrirOrdemServico(
            final AbrirAtendimentoCommand command,
            final Veiculo veiculo
    ) {
        final var ordemServico = OrdemServico.newOrdemServico(veiculo.getId(), command.relato());
        final var ordemServicoCriada = this.ordemServicoGateway.create(ordemServico);

        ordemServicoCriada.getDomainEvents().forEach(this.eventPublisher::publishEvent);
        ordemServicoCriada.clearEvents();

        return ordemServicoCriada;
    }

    private PessoaResultado obterOuCriarPessoa(final AbrirAtendimentoCommand command) {
        if (command.tipoPessoa() == TipoPessoaAtendimento.JURIDICA) {
            return obterOuCriarPessoaJuridica(command);
        }

        return obterOuCriarPessoaFisica(command);
    }

    private PessoaResultado obterOuCriarPessoaFisica(final AbrirAtendimentoCommand command) {
        validarPessoaFisica(command);

        final var cpf = CPF.from(command.cpf());

        return this.pessoaGateway.findPessoaFisicaByCpf(cpf)
                .map(pessoa -> new PessoaResultado(pessoa, false, null, false))
                .orElseGet(() -> new PessoaResultado((PessoaFisica) this.pessoaGateway.create(PessoaFisica.newPessoaFisica(
                        Email.from(command.email()),
                        Telefone.from(command.telefone()),
                        command.nome(),
                        cpf
                )), true, null, false));
    }

    private PessoaResultado obterOuCriarPessoaJuridica(final AbrirAtendimentoCommand command) {
        validarPessoaJuridica(command);

        final var cnpj = CNPJ.from(command.cnpj());

        return this.pessoaGateway.findPessoaJuridicaByCnpj(cnpj)
                .map(pessoa -> new PessoaResultado(pessoa, false, null, false))
                .orElseGet(() -> {
                    final var representanteLegal = obterOuCriarRepresentanteLegal(command);
                    final var pessoa = (PessoaJuridica) this.pessoaGateway.create(PessoaJuridica.newPessoaJuridica(
                        Email.from(command.email()),
                        Telefone.from(command.telefone()),
                        command.razaoSocial(),
                        cnpj,
                        representanteLegal.pessoa().getId()
                    ));

                    return new PessoaResultado(
                            pessoa,
                            true,
                            representanteLegal.pessoa(),
                            representanteLegal.criada()
                    );
                });
    }

    private RepresentanteLegalResultado obterOuCriarRepresentanteLegal(final AbrirAtendimentoCommand command) {
        final var cpf = CPF.from(command.representanteCpf());

        return this.pessoaGateway.findPessoaFisicaByCpf(cpf)
                .map(pessoa -> new RepresentanteLegalResultado(pessoa, false))
                .orElseGet(() -> new RepresentanteLegalResultado((PessoaFisica) this.pessoaGateway.create(PessoaFisica.newPessoaFisica(
                        Email.from(command.representanteEmail()),
                        Telefone.from(command.representanteTelefone()),
                        command.representanteNome(),
                        cpf
                )), true));
    }

    private void publicarEventosPessoa(final PessoaResultado pessoa) {
        if (pessoa.representanteLegalCriado()) {
            this.eventPublisher.publishEvent(new RepresentanteLegalAtendimentoCriadoEvent(
                    pessoa.representanteLegal().getId()
            ));
        }

        if (pessoa.criada()) {
            this.eventPublisher.publishEvent(new PessoaAtendimentoCriadaEvent(
                    pessoa.pessoa().getId(),
                    pessoa.pessoa() instanceof PessoaJuridica ? "JURIDICA" : "FISICA",
                    pessoa.representanteLegal() == null ? null : pessoa.representanteLegal().getId()
            ));
        }
    }

    private void publicarClienteCriado(final Cliente cliente) {
        this.eventPublisher.publishEvent(new ClienteAtendimentoCriadoEvent(
                cliente.getId(),
                cliente.getPessoaId()
        ));
    }

    private void publicarTipoVeiculoCriado(final TipoVeiculoResultado tipoVeiculo) {
        if (tipoVeiculo.criado()) {
            this.eventPublisher.publishEvent(new TipoVeiculoAtendimentoCriadoEvent(
                    tipoVeiculo.tipoVeiculo().getId()
            ));
        }
    }

    private void publicarVeiculoCriado(final Veiculo veiculo) {
        this.eventPublisher.publishEvent(new VeiculoAtendimentoCriadoEvent(
                veiculo.getId(),
                veiculo.getProprietarioId(),
                veiculo.getTipoVeiculoId()
        ));
    }

    private void publicarOrdemServicoAberta(final OrdemServico ordemServico) {
        this.eventPublisher.publishEvent(new OrdemServicoAtendimentoAbertaEvent(
                ordemServico.getId(),
                ordemServico.getVeiculoId()
        ));
    }

    private void validarPessoaFisica(final AbrirAtendimentoCommand command) {
        if (command.nome() == null || command.nome().isBlank()) {
            throw DomainException.with(new Error("Nome é obrigatório para pessoa física"));
        }

        if (command.cpf() == null || command.cpf().isBlank()) {
            throw DomainException.with(new Error("CPF é obrigatório para pessoa física"));
        }
    }

    private void validarPessoaJuridica(final AbrirAtendimentoCommand command) {
        if (command.razaoSocial() == null || command.razaoSocial().isBlank()) {
            throw DomainException.with(new Error("Razão social é obrigatória para pessoa jurídica"));
        }

        if (command.cnpj() == null || command.cnpj().isBlank()) {
            throw DomainException.with(new Error("CNPJ é obrigatório para pessoa jurídica"));
        }

        if (command.representanteNome() == null || command.representanteNome().isBlank()) {
            throw DomainException.with(new Error("Nome do representante legal é obrigatório para pessoa jurídica"));
        }

        if (command.representanteCpf() == null || command.representanteCpf().isBlank()) {
            throw DomainException.with(new Error("CPF do representante legal é obrigatório para pessoa jurídica"));
        }

        if (command.representanteEmail() == null || command.representanteEmail().isBlank()) {
            throw DomainException.with(new Error("Email do representante legal é obrigatório para pessoa jurídica"));
        }

        if (command.representanteTelefone() == null || command.representanteTelefone().isBlank()) {
            throw DomainException.with(new Error("Telefone do representante legal é obrigatório para pessoa jurídica"));
        }
    }

    private record PessoaResultado(
            Pessoa pessoa,
            boolean criada,
            PessoaFisica representanteLegal,
            boolean representanteLegalCriado
    ) {
    }

    private record RepresentanteLegalResultado(
            PessoaFisica pessoa,
            boolean criada
    ) {
    }

    private record TipoVeiculoResultado(
            TipoVeiculo tipoVeiculo,
            boolean criado
    ) {
    }
}
