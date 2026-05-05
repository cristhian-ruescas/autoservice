package com.autoservice.infrastructure.peca.query;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.peca.query.GetPecaByIdQuery;
import com.autoservice.application.peca.query.ListPecasQuery;
import com.autoservice.application.peca.query.PecaOutput;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.validation.Error;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PecaQueryService implements ListPecasQuery, GetPecaByIdQuery {

    private final EntityManager entityManager;

    public PecaQueryService(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationOutput<PecaOutput> listar(
            final int page,
            final int size,
            final String marca,
            final String codigo
    ) {
        validarPaginacao(page, size);
        final var marcaNormalizada = normalize(marca);
        final var codigoNormalizado = normalize(codigo);

        final var query = """
                select peca
                from Peca peca
                where (:marca is null or lower(peca.marca) like :marca)
                  and (:codigo is null or lower(peca.codigo) like :codigo)
                order by peca.marca asc, peca.codigo asc
                """;

        final var items = this.entityManager.createQuery(query, Peca.class)
                .setParameter("marca", marcaNormalizada)
                .setParameter("codigo", codigoNormalizado)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList()
                .stream()
                .map(PecaOutput::from)
                .toList();

        return PaginationOutput.from(items, page, size, totalPecas(marcaNormalizada, codigoNormalizado));
    }

    @Override
    @Transactional(readOnly = true)
    public PecaOutput buscarPorId(final UUID id) {
        if (id == null) {
            throw DomainException.with(new Error("Peça é obrigatória para consulta"));
        }

        final var query = """
                select peca
                from Peca peca
                where peca.id = :id
                """;

        final var rows = this.entityManager.createQuery(query, Peca.class)
                .setParameter("id", PecaID.from(id))
                .getResultList();

        if (rows.isEmpty()) {
            throw DomainException.with(new Error("Peça não encontrada"));
        }

        return PecaOutput.from(rows.getFirst());
    }

    private long totalPecas(final String marca, final String codigo) {
        final var query = """
                select count(peca)
                from Peca peca
                where (:marca is null or lower(peca.marca) like :marca)
                  and (:codigo is null or lower(peca.codigo) like :codigo)
                """;

        return this.entityManager.createQuery(query, Long.class)
                .setParameter("marca", marca)
                .setParameter("codigo", codigo)
                .getSingleResult();
    }

    private String normalize(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return "%" + value.trim().toLowerCase() + "%";
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
