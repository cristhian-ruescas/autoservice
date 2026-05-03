package com.autoservice.infrastructure.ordemcompra.persistence;

import com.autoservice.domain.ordemcompra.OrdemCompra;
import com.autoservice.domain.ordemcompra.OrdemCompraID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdemCompraRepository extends JpaRepository<OrdemCompra, OrdemCompraID> {
}
