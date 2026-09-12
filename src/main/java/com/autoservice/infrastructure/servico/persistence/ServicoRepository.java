package com.autoservice.infrastructure.servico.persistence;

import com.autoservice.infrastructure.persistence.entity.ServicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicoRepository extends JpaRepository<ServicoJpaEntity, String> {
}
