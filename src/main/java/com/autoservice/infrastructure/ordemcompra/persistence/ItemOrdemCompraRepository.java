package com.autoservice.infrastructure.ordemcompra.persistence;

import com.autoservice.domain.ordemcompra.ItemOrdemCompraID;
import com.autoservice.domain.ordemcompra.OrdemCompraID;
import com.autoservice.infrastructure.persistence.entity.ItemOrdemCompraJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemOrdemCompraRepository extends JpaRepository<ItemOrdemCompraJpaEntity, ItemOrdemCompraID> {

    List<ItemOrdemCompraJpaEntity> findByOrdemCompraId(OrdemCompraID ordemCompraId);
}
