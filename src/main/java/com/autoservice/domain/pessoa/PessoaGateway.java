package com.autoservice.domain.pessoa;

import com.autoservice.domain.pessoa.valueobject.CNPJ;
import com.autoservice.domain.pessoa.valueobject.CPF;

import java.util.Optional;

public interface PessoaGateway {

    Pessoa create(Pessoa pessoa);

    Pessoa update(Pessoa pessoa);

    Optional<Pessoa> findById(PessoaID id);

    Optional<PessoaFisica> findPessoaFisicaByCpf(CPF cpf);

    Optional<PessoaJuridica> findPessoaJuridicaByCnpj(CNPJ cnpj);
}
