package com.autoservice.infrastructure.ordemservico.persistence;

import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.infrastructure.persistence.entity.OrdemServicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServicoJpaEntity, String> {

    @Query("""
            select pf.cpf
            from OrdemServicoJpaEntity os
            join VeiculoJpaEntity v on v.id = os.veiculoId
            join ClienteJpaEntity c on c.pessoaId = v.proprietarioId
            join PessoaFisicaJpaEntity pf on pf.id = c.pessoaId
            where os.id = :id
            """)
    Optional<CPF> findProprietarioCpfById(@Param("id") String id);
}
