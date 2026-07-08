package com.autoservice.application.cliente.create;

import com.autoservice.application.UseCase;
import com.autoservice.application.atendimento.create.AbrirAtendimentoCommand;
import com.autoservice.application.atendimento.create.PessoaAtendimentoOrchestrator;
import com.autoservice.application.cliente.query.ClienteOutput;
import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteGateway;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.infrastructure.query.mapper.ClienteReadModelMapper;
import com.autoservice.validation.Error;

import java.util.List;
import java.util.Objects;

public class CadastrarClienteUseCase extends UseCase<CadastrarClienteCommand, ClienteOutput> {

    private final PessoaAtendimentoOrchestrator pessoaAtendimentoOrchestrator;
    private final ClienteGateway clienteGateway;

    public CadastrarClienteUseCase(
            final PessoaAtendimentoOrchestrator pessoaAtendimentoOrchestrator,
            final ClienteGateway clienteGateway
    ) {
        this.pessoaAtendimentoOrchestrator = Objects.requireNonNull(pessoaAtendimentoOrchestrator);
        this.clienteGateway = Objects.requireNonNull(clienteGateway);
    }

    @Override
    public ClienteOutput execute(final CadastrarClienteCommand command) {
        if (command == null) {
            throw DomainException.with(new Error("Comando para cadastrar cliente não deve ser nulo"));
        }

        final var pessoaResultado = this.pessoaAtendimentoOrchestrator.obterOuCriar(toAtendimentoCommand(command));
        final var pessoa = pessoaResultado.pessoa();

        this.clienteGateway.findByPessoaId(pessoa.getId())
                .ifPresent(cliente -> {
                    throw DomainException.with(new Error("Cliente já cadastrado para esta pessoa"));
                });

        final var cliente = this.clienteGateway.create(Cliente.newCliente(pessoa.getId()));

        return mapOutput(cliente, pessoa, pessoaResultado.representanteLegal());
    }

    private ClienteOutput mapOutput(
            final Cliente cliente,
            final com.autoservice.domain.pessoa.Pessoa pessoa,
            final PessoaFisica representanteLegal
    ) {
        if (pessoa instanceof PessoaJuridica pessoaJuridica) {
            return ClienteReadModelMapper.toClienteOutput(
                    cliente,
                    null,
                    pessoaJuridica,
                    representanteLegal
            );
        }

        return ClienteReadModelMapper.toClienteOutput(
                cliente,
                (PessoaFisica) pessoa,
                null,
                null
        );
    }

    private AbrirAtendimentoCommand toAtendimentoCommand(final CadastrarClienteCommand command) {
        return AbrirAtendimentoCommand.with(
                command.tipoPessoa(),
                command.nome(),
                command.cpf(),
                command.razaoSocial(),
                command.cnpj(),
                command.representanteNome(),
                command.representanteCpf(),
                command.representanteEmail(),
                command.representanteTelefone(),
                command.email(),
                command.telefone(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                List.of()
        );
    }
}
