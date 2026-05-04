package com.autoservice.infrastructure.pessoa;

import com.autoservice.domain.pessoa.Pessoa;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaGateway;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.domain.pessoa.valueobject.CNPJ;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.infrastructure.pessoa.persistence.PessoaJpaRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class PessoaGatewayImpl implements PessoaGateway {

    private final PessoaJpaRepository repository;

    public PessoaGatewayImpl(final PessoaJpaRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Pessoa create(final Pessoa pessoa) {
        return this.repository.save(pessoa);
    }

    @Override
    public Pessoa update(final Pessoa pessoa) {
        return this.repository.save(pessoa);
    }

    @Override
    public Optional<Pessoa> findById(final PessoaID id) {
        return this.repository.findById(id);
    }

    @Override
    public Optional<PessoaFisica> findPessoaFisicaByCpf(final CPF cpf) {
        return this.repository.findPessoaFisicaByCpf(cpf);
    }

    @Override
    public Optional<PessoaJuridica> findPessoaJuridicaByCnpj(final CNPJ cnpj) {
        return this.repository.findPessoaJuridicaByCnpj(cnpj);
    }
}
