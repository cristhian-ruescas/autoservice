package com.autoservice.application.cliente.update;

import com.autoservice.application.UseCase;
import com.autoservice.application.cliente.query.ClienteOutput;
import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteGateway;
import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.Pessoa;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaGateway;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import com.autoservice.validation.Error;
import com.autoservice.validation.handler.NotificationValidationHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AtualizarClienteUseCase extends UseCase<AtualizarClienteCommand, ClienteOutput> {

    private final ClienteGateway clienteGateway;
    private final PessoaGateway pessoaGateway;

    public AtualizarClienteUseCase(
            final ClienteGateway clienteGateway,
            final PessoaGateway pessoaGateway
    ) {
        this.clienteGateway = Objects.requireNonNull(clienteGateway);
        this.pessoaGateway = Objects.requireNonNull(pessoaGateway);
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

        return mapPessoaFisica(cliente, (PessoaFisica) this.pessoaGateway.update(pessoaAtualizada));
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

        return mapPessoaJuridica(cliente, pessoaSalva, representante);
    }

    private PessoaID obterRepresentanteLegalId(
            final PessoaJuridica pessoaJuridica,
            final AtualizarClienteCommand command
    ) {
        if (isBlank(command.representanteCpf())) {
            return pessoaJuridica.getRepresentanteLegalId();
        }

        validarRepresentanteLegal(command);

        final var cpf = CPF.from(command.representanteCpf());
        return this.pessoaGateway.findPessoaFisicaByCpf(cpf)
                .map(representante -> atualizarRepresentanteLegal(representante, command).getId())
                .orElseGet(() -> criarRepresentanteLegal(command, cpf).getId());
    }

    private PessoaFisica atualizarRepresentanteLegal(
            final PessoaFisica representante,
            final AtualizarClienteCommand command
    ) {
        final var pessoaAtualizada = PessoaFisica.withId(
                representante.getId(),
                Email.from(command.representanteEmail()),
                Telefone.from(command.representanteTelefone()),
                command.representanteNome(),
                CPF.from(command.representanteCpf())
        );
        validate(pessoaAtualizada);
        return (PessoaFisica) this.pessoaGateway.update(pessoaAtualizada);
    }

    private PessoaFisica criarRepresentanteLegal(
            final AtualizarClienteCommand command,
            final CPF cpf
    ) {
        return (PessoaFisica) this.pessoaGateway.create(PessoaFisica.newPessoaFisica(
                Email.from(command.representanteEmail()),
                Telefone.from(command.representanteTelefone()),
                command.representanteNome(),
                cpf
        ));
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

    private void validarRepresentanteLegal(final AtualizarClienteCommand command) {
        if (isBlank(command.representanteNome())) {
            throw DomainException.with(new Error("Nome do representante legal é obrigatório para pessoa jurídica"));
        }
        if (isBlank(command.representanteEmail())) {
            throw DomainException.with(new Error("Email do representante legal é obrigatório para pessoa jurídica"));
        }
        if (isBlank(command.representanteTelefone())) {
            throw DomainException.with(new Error("Telefone do representante legal é obrigatório para pessoa jurídica"));
        }
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

    private ClienteOutput mapPessoaFisica(
            final Cliente cliente,
            final PessoaFisica pessoaFisica
    ) {
        return new ClienteOutput(
                cliente.getId().getValue(),
                "FISICA",
                cliente.getDataCadastro().getValue(),
                pessoaFisica.getNome(),
                pessoaFisica.getCpf().getValue(),
                null,
                null,
                valueOf(pessoaFisica.getEmail()),
                valueOf(pessoaFisica.getTelefone()),
                null
        );
    }

    private ClienteOutput mapPessoaJuridica(
            final Cliente cliente,
            final PessoaJuridica pessoaJuridica,
            final PessoaFisica representante
    ) {
        return new ClienteOutput(
                cliente.getId().getValue(),
                "JURIDICA",
                cliente.getDataCadastro().getValue(),
                null,
                null,
                pessoaJuridica.getRazaoSocial(),
                pessoaJuridica.getCnpj().getValue(),
                valueOf(pessoaJuridica.getEmail()),
                valueOf(pessoaJuridica.getTelefone()),
                mapRepresentanteLegal(representante)
        );
    }

    private ClienteOutput.RepresentanteLegalOutput mapRepresentanteLegal(
            final PessoaFisica representante
    ) {
        if (representante == null) {
            return null;
        }

        return new ClienteOutput.RepresentanteLegalOutput(
                representante.getId().getValue(),
                representante.getNome(),
                representante.getCpf().getValue(),
                valueOf(representante.getEmail()),
                valueOf(representante.getTelefone())
        );
    }

    private String valueOf(final Object valueObject) {
        return valueObject == null ? null : valueObject.toString();
    }

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }
}
