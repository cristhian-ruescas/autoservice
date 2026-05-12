package com.autoservice.infrastructure.estoque.query;

import com.autoservice.application.PaginationOutput;
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

import java.util.UUID;

@Service
public class EstoqueQueryService implements EstoqueQuery {

    private final EntityManager entityManager;

    public EstoqueQueryService(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationOutput<EstoqueOutput> listar(final int page, final int size) {
        validarPaginacao(page, size);

        final var query = """
                select estoque, peca
                from Estoque estoque
                left join Peca peca on peca.estoqueId = estoque.id
                order by peca.descricao asc
                """;

        final var items = this.entityManager.createQuery(query, Object[].class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList()
                .stream()
                .map(this::map)
                .toList();

        return PaginationOutput.from(items, page, size, totalEstoques());
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

    private long totalEstoques() {
        final var query = """
                select count(estoque)
                from Estoque estoque
                """;

        return this.entityManager.createQuery(query, Long.class).getSingleResult();
    }

    private void validarPaginacao(final int page, final int size) {
        if (page < 0) {
            throw DomainException.with(new Error("Página não deve ser menor que zero"));
        }
        if (size <= 0) {
            throw DomainException.with(new Error("Tamanho da página deve ser maior que zero"));
        }
        if (size > 100) {
            throw DomainException.with(new Error("Tamanho da página não deve ser maior que 100"));
        }
    }
}
