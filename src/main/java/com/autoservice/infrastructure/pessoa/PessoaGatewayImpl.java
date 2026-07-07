package com.autoservice.infrastructure.pessoa;

import com.autoservice.domain.pessoa.*;
import com.autoservice.domain.pessoa.valueobject.CNPJ;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.infrastructure.persistence.mapper.PessoaMapper;
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
        this.repository.save(PessoaMapper.toEntity(pessoa));
        return pessoa;
    }

    @Override
    public Pessoa update(final Pessoa pessoa) {
        this.repository.save(PessoaMapper.toEntity(pessoa));
        return pessoa;
    }

    @Override
    public Optional<Pessoa> findById(final PessoaID id) {
        return this.repository.findById(id.getValue()).map(PessoaMapper::toDomain);
    }

    @Override
    public Optional<PessoaFisica> findPessoaFisicaByCpf(final CPF cpf) {
        return this.repository.findPessoaFisicaByCpf(cpf).map(PessoaMapper::toDomain).map(PessoaFisica.class::cast);
    }

    @Override
    public Optional<PessoaJuridica> findPessoaJuridicaByCnpj(final CNPJ cnpj) {
        return this.repository.findPessoaJuridicaByCnpj(cnpj).map(PessoaMapper::toDomain).map(PessoaJuridica.class::cast);
    }
}
