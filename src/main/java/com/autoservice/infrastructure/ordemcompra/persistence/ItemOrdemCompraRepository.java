package com.autoservice.infrastructure.ordemcompra.persistence;

import com.autoservice.domain.ordemcompra.ItemOrdemCompra;
import com.autoservice.domain.ordemcompra.ItemOrdemCompraID;
import com.autoservice.domain.ordemcompra.OrdemCompraID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemOrdemCompraRepository extends JpaRepository<ItemOrdemCompra, ItemOrdemCompraID> {

    List<ItemOrdemCompra> findByOrdemCompraId(OrdemCompraID ordemCompraId);
}
