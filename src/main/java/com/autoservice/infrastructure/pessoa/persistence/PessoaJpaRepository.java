package com.autoservice.infrastructure.pessoa.persistence;

import com.autoservice.domain.pessoa.Pessoa;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.domain.pessoa.valueobject.CNPJ;
import com.autoservice.domain.pessoa.valueobject.CPF;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaJpaRepository extends JpaRepository<Pessoa, PessoaID> {

    @Query("select pf from PessoaFisica pf where pf.cpf = :cpf")
    Optional<PessoaFisica> findPessoaFisicaByCpf(@Param("cpf") CPF cpf);

    @Query("select pj from PessoaJuridica pj where pj.cnpj = :cnpj")
    Optional<PessoaJuridica> findPessoaJuridicaByCnpj(@Param("cnpj") CNPJ cnpj);
}
