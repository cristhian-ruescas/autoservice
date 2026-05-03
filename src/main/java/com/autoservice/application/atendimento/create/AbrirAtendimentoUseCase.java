package com.autoservice.application.atendimento.create;

import com.autoservice.application.UseCase;
import com.autoservice.application.atendimento.create.enums.TipoPessoaAtendimento;
import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteGateway;
import com.autoservice.domain.events.DomainEventPublisher;
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
        final var pessoaCriada = obterOuCriarPessoa(command);

        final var cliente = Cliente.newCliente(pessoaCriada.getId());
        final var clienteCriado = this.clienteGateway.create(cliente);
        final var tipoVeiculo = obterOuCriarTipoVeiculo(command);

        final var veiculo = Veiculo.newVeiculo(
                pessoaCriada.getId(),
                tipoVeiculo.getId(),
                Placa.from(command.placa()),
                Cor.from(command.cor()),
                Kilometragem.from(command.kilometragem())
        );
        final var veiculoCriado = this.veiculoGateway.create(veiculo);

        final var ordemServico = OrdemServico.newOrdemServico(veiculoCriado.getId(), command.relato());
        final var ordemServicoCriada = this.ordemServicoGateway.create(ordemServico);

        ordemServicoCriada.getDomainEvents().forEach(this.eventPublisher::publishEvent);
        ordemServicoCriada.clearEvents();

        return AbrirAtendimentoOutput.from(
                pessoaCriada,
                clienteCriado,
                veiculoCriado,
                ordemServicoCriada
        );
    }

    private TipoVeiculo obterOuCriarTipoVeiculo(final AbrirAtendimentoCommand command) {
        return this.tipoVeiculoGateway.findByMarcaModeloAno(command.marca(), command.modelo(), command.ano())
                .orElseGet(() -> this.tipoVeiculoGateway.create(TipoVeiculo.newTipoVeiculo(
                        Marca.from(command.marca()),
                        Modelo.from(command.modelo()),
                        Ano.from(command.ano())
                )));
    }

    private Pessoa obterOuCriarPessoa(final AbrirAtendimentoCommand command) {
        if (command.tipoPessoa() == TipoPessoaAtendimento.JURIDICA) {
            return obterOuCriarPessoaJuridica(command);
        }

        return obterOuCriarPessoaFisica(command);
    }

    private PessoaFisica obterOuCriarPessoaFisica(final AbrirAtendimentoCommand command) {
        validarPessoaFisica(command);

        final var cpf = CPF.from(command.cpf());

        return this.pessoaGateway.findPessoaFisicaByCpf(cpf)
                .orElseGet(() -> (PessoaFisica) this.pessoaGateway.create(PessoaFisica.newPessoaFisica(
                        Email.from(command.email()),
                        Telefone.from(command.telefone()),
                        command.nome(),
                        cpf
                )));
    }

    private PessoaJuridica obterOuCriarPessoaJuridica(final AbrirAtendimentoCommand command) {
        validarPessoaJuridica(command);

        final var cnpj = CNPJ.from(command.cnpj());

        return this.pessoaGateway.findPessoaJuridicaByCnpj(cnpj)
                .orElseGet(() -> (PessoaJuridica) this.pessoaGateway.create(PessoaJuridica.newPessoaJuridica(
                        Email.from(command.email()),
                        Telefone.from(command.telefone()),
                        command.razaoSocial(),
                        cnpj,
                        obterOuCriarRepresentanteLegal(command).getId()
                )));
    }

    private PessoaFisica obterOuCriarRepresentanteLegal(final AbrirAtendimentoCommand command) {
        final var cpf = CPF.from(command.representanteCpf());

        return this.pessoaGateway.findPessoaFisicaByCpf(cpf)
                .orElseGet(() -> (PessoaFisica) this.pessoaGateway.create(PessoaFisica.newPessoaFisica(
                        Email.from(command.representanteEmail()),
                        Telefone.from(command.representanteTelefone()),
                        command.representanteNome(),
                        cpf
                )));
    }

    private void validarPessoaFisica(final AbrirAtendimentoCommand command) {
        if (command.nome() == null || command.nome().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório para pessoa física");
        }

        if (command.cpf() == null || command.cpf().isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório para pessoa física");
        }
    }

    private void validarPessoaJuridica(final AbrirAtendimentoCommand command) {
        if (command.razaoSocial() == null || command.razaoSocial().isBlank()) {
            throw new IllegalArgumentException("Razão social é obrigatória para pessoa jurídica");
        }

        if (command.cnpj() == null || command.cnpj().isBlank()) {
            throw new IllegalArgumentException("CNPJ é obrigatório para pessoa jurídica");
        }

        if (command.representanteNome() == null || command.representanteNome().isBlank()) {
            throw new IllegalArgumentException("Nome do representante legal é obrigatório para pessoa jurídica");
        }

        if (command.representanteCpf() == null || command.representanteCpf().isBlank()) {
            throw new IllegalArgumentException("CPF do representante legal é obrigatório para pessoa jurídica");
        }

        if (command.representanteEmail() == null || command.representanteEmail().isBlank()) {
            throw new IllegalArgumentException("Email do representante legal é obrigatório para pessoa jurídica");
        }

        if (command.representanteTelefone() == null || command.representanteTelefone().isBlank()) {
            throw new IllegalArgumentException("Telefone do representante legal é obrigatório para pessoa jurídica");
        }
    }
}
