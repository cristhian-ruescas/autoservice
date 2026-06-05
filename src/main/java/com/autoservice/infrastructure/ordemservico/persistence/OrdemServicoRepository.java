package com.autoservice.infrastructure.ordemservico.persistence;

import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.infrastructure.persistence.entity.OrdemServicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServicoJpaEntity, OrdemServicoID> {
}
