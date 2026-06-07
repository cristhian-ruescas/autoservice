package com.autoservice.application.cliente.update;

import com.autoservice.application.UseCase;
import com.autoservice.application.cliente.query.ClienteOutput;
import com.autoservice.application.pessoa.RepresentanteLegalOrchestrator;
import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteGateway;
import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.*;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import com.autoservice.infrastructure.query.mapper.ClienteReadModelMapper;
import com.autoservice.validation.Error;
import com.autoservice.validation.handler.NotificationValidationHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AtualizarClienteUseCase extends UseCase<AtualizarClienteCommand, ClienteOutput> {

    private final ClienteGateway clienteGateway;
    private final PessoaGateway pessoaGateway;
    private final RepresentanteLegalOrchestrator representanteLegalOrchestrator;

    public AtualizarClienteUseCase(
            final ClienteGateway clienteGateway,
            final PessoaGateway pessoaGateway,
            final RepresentanteLegalOrchestrator representanteLegalOrchestrator
    ) {
        this.clienteGateway = Objects.requireNonNull(clienteGateway);
        this.pessoaGateway = Objects.requireNonNull(pessoaGateway);
        this.representanteLegalOrchestrator = Objects.requireNonNull(representanteLegalOrchestrator);
    }

    @Override
    @Transactional
    public ClienteOutput execute(final AtualizarClienteCommand command) {
        if (command == null) {
            throw DomainException.with(new Error("Comando para atualizar cliente não deve ser nulo"));
        }
        if (command.clienteId() == null) {
            throw DomainException.with(new Error("Cliente é obrigatório para atualização"));
        }

        final var cliente = this.clienteGateway.findById(ClienteID.from(command.clienteId()))
                .orElseThrow(() -> DomainException.with(new Error("Cliente não encontrado")));

        final var pessoa = this.pessoaGateway.findById(cliente.getPessoaId())
                .orElseThrow(() -> DomainException.with(new Error("Pessoa do cliente não encontrada")));

        if (pessoa instanceof PessoaFisica pessoaFisica) {
            return atualizarPessoaFisica(cliente, pessoaFisica, command);
        }
        if (pessoa instanceof PessoaJuridica pessoaJuridica) {
            return atualizarPessoaJuridica(cliente, pessoaJuridica, command);
        }

        throw DomainException.with(new Error("Tipo de pessoa do cliente não suportado"));
    }

    private ClienteOutput atualizarPessoaFisica(
            final Cliente cliente,
            final PessoaFisica pessoaFisica,
            final AtualizarClienteCommand command
    ) {
        validarPessoaFisica(command);

        final var pessoaAtualizada = PessoaFisica.withId(
                pessoaFisica.getId(),
                Email.from(command.email()),
                Telefone.from(command.telefone()),
                command.nome(),
                pessoaFisica.getCpf()
        );
        validate(pessoaAtualizada);

        return ClienteReadModelMapper.toClienteOutput(
                cliente,
                (PessoaFisica) this.pessoaGateway.update(pessoaAtualizada),
                null,
                null
        );
    }

    private ClienteOutput atualizarPessoaJuridica(
            final Cliente cliente,
            final PessoaJuridica pessoaJuridica,
            final AtualizarClienteCommand command
    ) {
        validarPessoaJuridica(command);

        final var representanteLegalId = obterRepresentanteLegalId(pessoaJuridica, command);
        final var pessoaAtualizada = PessoaJuridica.withId(
                pessoaJuridica.getId(),
                Email.from(command.email()),
                Telefone.from(command.telefone()),
                command.razaoSocial(),
                pessoaJuridica.getCnpj(),
                representanteLegalId
        );
        validate(pessoaAtualizada);

        final var pessoaSalva = (PessoaJuridica) this.pessoaGateway.update(pessoaAtualizada);
        final var representante = representanteLegalId == null
                ? null
                : this.pessoaGateway.findById(representanteLegalId)
                  .filter(PessoaFisica.class::isInstance)
                  .map(PessoaFisica.class::cast)
                  .orElse(null);

        return ClienteReadModelMapper.toClienteOutput(cliente, null, pessoaSalva, representante);
    }

    private PessoaID obterRepresentanteLegalId(
            final PessoaJuridica pessoaJuridica,
            final AtualizarClienteCommand command
    ) {
        if (isBlank(command.representanteCpf())) {
            return pessoaJuridica.getRepresentanteLegalId();
        }

        final var cpf = CPF.from(command.representanteCpf());
        return this.pessoaGateway.findPessoaFisicaByCpf(cpf)
                .map(representante -> this.representanteLegalOrchestrator
                        .atualizar(
                                representante,
                                command.representanteNome(),
                                command.representanteCpf(),
                                command.representanteEmail(),
                                command.representanteTelefone()
                        )
                        .getId())
                .orElseGet(() -> this.representanteLegalOrchestrator.obterOuCriar(
                        command.representanteNome(),
                        command.representanteCpf(),
                        command.representanteEmail(),
                        command.representanteTelefone()
                ).pessoa().getId());
    }

    private void validarPessoaFisica(final AtualizarClienteCommand command) {
        if (isBlank(command.nome())) {
            throw DomainException.with(new Error("Nome é obrigatório para pessoa física"));
        }
        validarContato(command.email(), command.telefone());
    }

    private void validarPessoaJuridica(final AtualizarClienteCommand command) {
        if (isBlank(command.razaoSocial())) {
            throw DomainException.with(new Error("Razão social é obrigatória para pessoa jurídica"));
        }
        validarContato(command.email(), command.telefone());
    }

    private void validarContato(final String email, final String telefone) {
        if (isBlank(email)) {
            throw DomainException.with(new Error("Email é obrigatório para atualização do cliente"));
        }
        if (isBlank(telefone)) {
            throw DomainException.with(new Error("Telefone é obrigatório para atualização do cliente"));
        }
    }

    private void validate(final Pessoa pessoa) {
        final var handler = new NotificationValidationHandler();
        pessoa.validate(handler);

        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
    }

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }
}
