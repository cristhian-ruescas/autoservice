package com.autoservice.infrastructure.pessoa.persistence;

import com.autoservice.domain.pessoa.valueobject.CNPJ;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.infrastructure.persistence.entity.PessoaFisicaJpaEntity;
import com.autoservice.infrastructure.persistence.entity.PessoaJpaEntity;
import com.autoservice.infrastructure.persistence.entity.PessoaJuridicaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaJpaRepository extends JpaRepository<PessoaJpaEntity, String> {

    @Query("select pf from PessoaFisicaJpaEntity pf where pf.cpf = :cpf")
    Optional<PessoaFisicaJpaEntity> findPessoaFisicaByCpf(@Param("cpf") CPF cpf);

    @Query("select pj from PessoaJuridicaJpaEntity pj where pj.cnpj = :cnpj")
    Optional<PessoaJuridicaJpaEntity> findPessoaJuridicaByCnpj(@Param("cnpj") CNPJ cnpj);
}
