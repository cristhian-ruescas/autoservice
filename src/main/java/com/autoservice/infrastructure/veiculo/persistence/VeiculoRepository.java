package com.autoservice.infrastructure.veiculo.persistence;

import com.autoservice.infrastructure.persistence.entity.VeiculoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeiculoRepository extends JpaRepository<VeiculoJpaEntity, String> {
}
