package com.autoservice.infrastructure.itemservico.persistence;

import com.autoservice.domain.itemservico.ItemServico;
import com.autoservice.domain.itemservico.ItemServicoID;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ItemServicoRepository extends JpaRepository<ItemServico, ItemServicoID> {

    @Query("""
            select coalesce(sum(item.valorUnitario * item.quantidade), 0)
            from ItemServico item
            where item.ordemServicoId = :ordemServicoId
            """)
    BigDecimal totalByOrdemServicoId(@Param("ordemServicoId") OrdemServicoID ordemServicoId);

    List<ItemServico> findByOrdemServicoId(OrdemServicoID ordemServicoId);
}
