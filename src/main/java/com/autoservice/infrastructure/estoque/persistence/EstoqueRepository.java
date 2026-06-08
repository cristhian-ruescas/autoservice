package com.autoservice.infrastructure.estoque.persistence;

import com.autoservice.infrastructure.persistence.entity.EstoqueJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstoqueRepository extends JpaRepository<EstoqueJpaEntity, String> {
}
