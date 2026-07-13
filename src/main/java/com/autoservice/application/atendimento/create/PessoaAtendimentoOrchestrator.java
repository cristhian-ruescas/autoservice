package com.autoservice.application.atendimento.create;

import com.autoservice.application.pessoa.RepresentanteLegalOrchestrator;
import com.autoservice.application.atendimento.create.enums.TipoPessoaAtendimento;
import com.autoservice.application.pessoa.TipoPessoaCodigo;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.Pessoa;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaGateway;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.domain.pessoa.valueobject.CNPJ;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import com.autoservice.validation.Error;

import java.util.Objects;

public final class PessoaAtendimentoOrchestrator {

    private final PessoaGateway pessoaGateway;
    private final RepresentanteLegalOrchestrator representanteLegalOrchestrator;

    public PessoaAtendimentoOrchestrator(
            final PessoaGateway pessoaGateway,
            final RepresentanteLegalOrchestrator representanteLegalOrchestrator
    ) {
        this.pessoaGateway = Objects.requireNonNull(pessoaGateway);
        this.representanteLegalOrchestrator = Objects.requireNonNull(representanteLegalOrchestrator);
    }

    public PessoaResultado obterOuCriar(final AbrirAtendimentoCommand command) {
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
                .orElseGet(() -> new PessoaResultado(this.pessoaGateway.create(PessoaFisica.newPessoaFisica(
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

    private RepresentanteLegalOrchestrator.Resultado obterOuCriarRepresentanteLegal(
            final AbrirAtendimentoCommand command
    ) {
        return this.representanteLegalOrchestrator.obterOuCriar(
                command.representanteNome(),
                command.representanteCpf(),
                command.representanteEmail(),
                command.representanteTelefone()
        );
    }

    public String tipoPessoaCodigo(final Pessoa pessoa) {
        return pessoa instanceof PessoaJuridica ? TipoPessoaCodigo.JURIDICA : TipoPessoaCodigo.FISICA;
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

    public record PessoaResultado(
            Pessoa pessoa,
            boolean criada,
            PessoaFisica representanteLegal,
            boolean representanteLegalCriado
    ) {
    }
}
