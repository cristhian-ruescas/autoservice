package com.autoservice.infrastructure.peca.persistence;

import com.autoservice.infrastructure.persistence.entity.PecaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PecaRepository extends JpaRepository<PecaJpaEntity, String> {
}
