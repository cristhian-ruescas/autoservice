package com.autoservice.infrastructure.estoque.query;

import com.autoservice.application.estoque.query.EstoqueOutput;
import com.autoservice.application.estoque.query.EstoqueQuery;
import com.autoservice.domain.estoque.Estoque;
import com.autoservice.domain.estoque.EstoqueID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.peca.Peca;
import com.autoservice.validation.Error;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EstoqueQueryService implements EstoqueQuery {

    private final EntityManager entityManager;

    public EstoqueQueryService(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstoqueOutput> listar() {
        final var query = """
                select estoque, peca
                from Estoque estoque
                left join Peca peca on peca.estoqueId = estoque.id
                order by peca.descricao asc
                """;

        return this.entityManager.createQuery(query, Object[].class)
                .getResultList()
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EstoqueOutput detalhar(final UUID id) {
        final var query = """
                select estoque, peca
                from Estoque estoque
                left join Peca peca on peca.estoqueId = estoque.id
                where estoque.id = :id
                """;

        final var rows = this.entityManager.createQuery(query, Object[].class)
                .setParameter("id", EstoqueID.from(id))
                .getResultList();

        if (rows.isEmpty()) {
            throw DomainException.with(new Error("Estoque não encontrado"));
        }

        return map(rows.getFirst());
    }

    private EstoqueOutput map(final Object[] row) {
        final var estoque = (Estoque) row[0];
        final var peca = (Peca) row[1];

        return new EstoqueOutput(
                estoque.getId().getValue(),
                estoque.getQuantidadeDisponivel(),
                estoque.getQuantidadeMinima(),
                estoque.getLocalizacao(),
                mapPeca(peca)
        );
    }

    private EstoqueOutput.PecaOutput mapPeca(final Peca peca) {
        if (peca == null) {
            return null;
        }

        return new EstoqueOutput.PecaOutput(
                peca.getId().getValue(),
                peca.getCodigo(),
                peca.getDescricao(),
                peca.getMarca()
        );
    }
}
