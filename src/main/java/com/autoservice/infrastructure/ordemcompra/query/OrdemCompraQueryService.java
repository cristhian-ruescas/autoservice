package com.autoservice.infrastructure.ordemcompra.query;

import com.autoservice.application.ordemcompra.query.OrdemCompraOutput;
import com.autoservice.application.ordemcompra.query.OrdemCompraQuery;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemcompra.ItemOrdemCompra;
import com.autoservice.domain.ordemcompra.OrdemCompra;
import com.autoservice.domain.ordemcompra.OrdemCompraID;
import com.autoservice.domain.peca.Peca;
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
                from OrdemCompra ordemCompra
                order by ordemCompra.dataCompra.value desc
                """;

        return this.entityManager.createQuery(query, OrdemCompra.class)
                .getResultList()
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrdemCompraOutput detalhar(final UUID id) {
        final var ordemCompraId = OrdemCompraID.from(id);

        final var ordemCompra = this.entityManager.find(OrdemCompra.class, ordemCompraId);

        if (ordemCompra == null) {
            throw DomainException.with(new Error("Ordem de compra não encontrada"));
        }

        return map(ordemCompra);
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
                from ItemOrdemCompra item
                join Peca peca on peca.embeddedId = item.pecaId
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
        final var item = (ItemOrdemCompra) row[0];
        final var peca = (Peca) row[1];

        return new OrdemCompraOutput.ItemOutput(
                item.getId().getValue(),
                peca.getId().getValue(),
                peca.getCodigo(),
                peca.getDescricao(),
                item.getQuantidade()
        );
    }
}
