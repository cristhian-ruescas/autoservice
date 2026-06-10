package com.autoservice.application.pessoa;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaGateway;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import com.autoservice.validation.Error;
import com.autoservice.validation.handler.NotificationValidationHandler;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public final class RepresentanteLegalOrchestrator {

    private final PessoaGateway pessoaGateway;

    public RepresentanteLegalOrchestrator(final PessoaGateway pessoaGateway) {
        this.pessoaGateway = Objects.requireNonNull(pessoaGateway);
    }

    public Resultado obterOuCriar(
            final String nome,
            final String cpf,
            final String email,
            final String telefone
    ) {
        validarDados(nome, cpf, email, telefone);

        final var cpfVo = CPF.from(cpf);

        return this.pessoaGateway.findPessoaFisicaByCpf(cpfVo)
                .map(pessoa -> new Resultado(pessoa, false))
                .orElseGet(() -> new Resultado(criar(nome, cpfVo, email, telefone), true));
    }

    public PessoaFisica atualizar(
            final PessoaFisica representante,
            final String nome,
            final String cpf,
            final String email,
            final String telefone
    ) {
        validarDados(nome, cpf, email, telefone);

        final var pessoaAtualizada = PessoaFisica.withId(
                representante.getId(),
                Email.from(email),
                Telefone.from(telefone),
                nome,
                CPF.from(cpf)
        );
        validar(pessoaAtualizada);

        return (PessoaFisica) this.pessoaGateway.update(pessoaAtualizada);
    }

    private PessoaFisica criar(
            final String nome,
            final CPF cpf,
            final String email,
            final String telefone
    ) {
        return (PessoaFisica) this.pessoaGateway.create(PessoaFisica.newPessoaFisica(
                Email.from(email),
                Telefone.from(telefone),
                nome,
                cpf
        ));
    }

    private void validarDados(
            final String nome,
            final String cpf,
            final String email,
            final String telefone
    ) {
        if (isBlank(nome)) {
            throw DomainException.with(new Error("Nome do representante legal é obrigatório"));
        }
        if (isBlank(cpf)) {
            throw DomainException.with(new Error("CPF do representante legal é obrigatório"));
        }
        if (isBlank(email)) {
            throw DomainException.with(new Error("Email do representante legal é obrigatório"));
        }
        if (isBlank(telefone)) {
            throw DomainException.with(new Error("Telefone do representante legal é obrigatório"));
        }
    }

    private void validar(final PessoaFisica pessoa) {
        final var handler = new NotificationValidationHandler();
        pessoa.validate(handler);

        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
    }

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }

    public record Resultado(
            PessoaFisica pessoa,
            boolean criada
    ) {
    }
}
