package com.autoservice.infrastructure.itemservico.persistence;

import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.infrastructure.persistence.entity.ItemServicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ItemServicoRepository extends JpaRepository<ItemServicoJpaEntity, String> {

    @Query("""
            select coalesce(sum(item.valorUnitario * item.quantidade), 0)
            from ItemServicoJpaEntity item
            where item.ordemServicoId = :ordemServicoId
            """)
    BigDecimal totalByOrdemServicoId(@Param("ordemServicoId") String ordemServicoId);

    List<ItemServicoJpaEntity> findByOrdemServicoId(String ordemServicoId);

    @Query("""
            select count(item) > 0
            from ItemServicoJpaEntity item
            join OrdemServicoJpaEntity ordemServico on ordemServico.id = item.ordemServicoId
            where item.pecaId = :pecaId
              and ordemServico.status <> :status
            """)
    boolean existsByPecaIdAndOrdemServicoStatusNot(
            @Param("pecaId") String pecaId,
            @Param("status") OrdemServicoStatus status
    );
}
