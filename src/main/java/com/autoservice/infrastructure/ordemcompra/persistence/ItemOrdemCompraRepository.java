package com.autoservice.infrastructure.ordemcompra.persistence;

import com.autoservice.infrastructure.persistence.entity.ItemOrdemCompraJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemOrdemCompraRepository extends JpaRepository<ItemOrdemCompraJpaEntity, String> {

    List<ItemOrdemCompraJpaEntity> findByOrdemCompraId(String ordemCompraId);
}
