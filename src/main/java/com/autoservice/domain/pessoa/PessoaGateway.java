package com.autoservice.domain.pessoa;

import com.autoservice.domain.pessoa.valueobject.CNPJ;
import com.autoservice.domain.pessoa.valueobject.CPF;

import java.util.Optional;

public interface PessoaGateway {

    Pessoa create(Pessoa pessoa);

    Optional<PessoaFisica> findPessoaFisicaByCpf(CPF cpf);

    Optional<PessoaJuridica> findPessoaJuridicaByCnpj(CNPJ cnpj);
}
