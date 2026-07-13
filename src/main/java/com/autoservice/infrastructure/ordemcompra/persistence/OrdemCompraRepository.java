package com.autoservice.infrastructure.ordemcompra.persistence;

import com.autoservice.infrastructure.persistence.entity.OrdemCompraJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdemCompraRepository extends JpaRepository<OrdemCompraJpaEntity, String> {
}
