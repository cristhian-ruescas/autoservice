package com.autoservice.infrastructure.ordemcompra.query;

import com.autoservice.application.ordemcompra.query.OrdemCompraOutput;
import com.autoservice.application.ordemcompra.query.OrdemCompraQuery;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemcompra.OrdemCompra;
import com.autoservice.domain.ordemcompra.OrdemCompraID;
import com.autoservice.infrastructure.persistence.entity.ItemOrdemCompraJpaEntity;
import com.autoservice.infrastructure.persistence.entity.OrdemCompraJpaEntity;
import com.autoservice.infrastructure.persistence.entity.PecaJpaEntity;
import com.autoservice.infrastructure.persistence.mapper.ItemOrdemCompraMapper;
import com.autoservice.infrastructure.persistence.mapper.OrdemCompraMapper;
import com.autoservice.infrastructure.persistence.mapper.PecaMapper;
import com.autoservice.validation.Error;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrdemCompraQueryService implements OrdemCompraQuery {

    private final EntityManager entityManager;

    public OrdemCompraQueryService(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemCompraOutput> listar() {
        final var query = """
                select ordemCompra
                from OrdemCompraJpaEntity ordemCompra
                order by ordemCompra.dataCompra desc
                """;

        return this.entityManager.createQuery(query, OrdemCompraJpaEntity.class)
                .getResultList()
                .stream()
                .map(entity -> map(OrdemCompraMapper.toDomain(entity)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrdemCompraOutput detalhar(final UUID id) {
        final var ordemCompraId = OrdemCompraID.from(id);

        final var ordemCompra = this.entityManager.find(OrdemCompraJpaEntity.class, ordemCompraId);

        if (ordemCompra == null) {
            throw DomainException.with(new Error("Ordem de compra não encontrada"));
        }

        return map(OrdemCompraMapper.toDomain(ordemCompra));
    }

    private OrdemCompraOutput map(final OrdemCompra ordemCompra) {
        return new OrdemCompraOutput(
                ordemCompra.getId().getValue(),
                ordemCompra.getStatus().name(),
                ordemCompra.getDataCompra().getValue(),
                buscarItens(ordemCompra.getId())
        );
    }

    private List<OrdemCompraOutput.ItemOutput> buscarItens(final OrdemCompraID ordemCompraId) {
        final var query = """
                select item, peca
                from ItemOrdemCompraJpaEntity item
                join PecaJpaEntity peca on peca.id = item.pecaId
                where item.ordemCompraId = :ordemCompraId
                order by peca.descricao asc
                """;

        return this.entityManager.createQuery(query, Object[].class)
                .setParameter("ordemCompraId", ordemCompraId)
                .getResultList()
                .stream()
                .map(this::mapItem)
                .toList();
    }

    private OrdemCompraOutput.ItemOutput mapItem(final Object[] row) {
        final var item = ItemOrdemCompraMapper.toDomain((ItemOrdemCompraJpaEntity) row[0]);
        final var peca = PecaMapper.toDomain((PecaJpaEntity) row[1]);

        return new OrdemCompraOutput.ItemOutput(
                item.getId().getValue(),
                peca.getId().getValue(),
                peca.getCodigo(),
                peca.getDescricao(),
                item.getQuantidade()
        );
    }
}
