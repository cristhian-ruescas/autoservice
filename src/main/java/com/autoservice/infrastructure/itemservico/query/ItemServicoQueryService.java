package com.autoservice.infrastructure.itemservico.query;

import com.autoservice.application.ordemservico.itemservico.AdicionarItemServicoOutput;
import com.autoservice.application.ordemservico.itemservico.ListItensServicoQuery;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ItemServicoQueryService implements ListItensServicoQuery {

    private final EntityManager entityManager;

    public ItemServicoQueryService(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdicionarItemServicoOutput> execute(final UUID ordemServicoId) {
        final var query = """
                select item
                from ItemServicoJpaEntity item
                where item.ordemServicoId = :ordemServicoId
                order by item.tipo asc, item.descricao asc
                """;

        return this.entityManager.createQuery(query, com.autoservice.domain.itemservico.ItemServico.class)
                .setParameter("ordemServicoId", OrdemServicoID.from(ordemServicoId))
                .getResultList()
                .stream()
                .map(AdicionarItemServicoOutput::from)
                .toList();
    }
}
