package com.autoservice.infrastructure.tipoveiculo.query;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.tipoveiculo.query.GetTipoVeiculoByIdQuery;
import com.autoservice.application.tipoveiculo.query.ListTipoVeiculoQuery;
import com.autoservice.application.tipoveiculo.query.TipoVeiculoOutput;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.infrastructure.persistence.entity.TipoVeiculoJpaEntity;
import com.autoservice.infrastructure.persistence.mapper.TipoVeiculoMapper;
import com.autoservice.infrastructure.query.PaginacaoValidator;
import com.autoservice.infrastructure.query.QueryFilterNormalizer;
import com.autoservice.validation.Error;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TipoVeiculoQueryService implements ListTipoVeiculoQuery, GetTipoVeiculoByIdQuery {

    private final EntityManager entityManager;

    public TipoVeiculoQueryService(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationOutput<TipoVeiculoOutput> listar(
            final int page,
            final int size,
            final String marca,
            final String modelo,
            final Integer ano
    ) {
        PaginacaoValidator.validar(page, size);
        final var marcaNormalizada = QueryFilterNormalizer.buscaParcial(marca);
        final var modeloNormalizado = QueryFilterNormalizer.buscaParcial(modelo);

        final var query = """
                select tipo
                from TipoVeiculoJpaEntity tipo
                where (:marca is null or lower(tipo.marca) like :marca)
                  and (:modelo is null or lower(tipo.modelo) like :modelo)
                  and (:ano is null or tipo.ano = :ano)
                order by tipo.marca asc, tipo.modelo asc, tipo.ano desc
                """;

        final List<TipoVeiculoOutput> items = this.entityManager.createQuery(query, TipoVeiculoJpaEntity.class)
                .setParameter("marca", marcaNormalizada)
                .setParameter("modelo", modeloNormalizado)
                .setParameter("ano", ano)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList()
                .stream()
                .map(entity -> TipoVeiculoOutput.from(TipoVeiculoMapper.toDomain(entity)))
                .toList();

        return PaginationOutput.from(
                items,
                page,
                size,
                totalTiposVeiculo(marcaNormalizada, modeloNormalizado, ano)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TipoVeiculoOutput buscarPorId(final UUID id) {
        if (id == null) {
            throw DomainException.with(new Error("Tipo de veículo é obrigatório para consulta"));
        }

        final var query = """
                select tipo
                from TipoVeiculoJpaEntity tipo
                where tipo.id = :id
                """;

        final var rows = this.entityManager.createQuery(query, TipoVeiculoJpaEntity.class)
                .setParameter("id", TipoVeiculoID.from(id).getValue())
                .getResultList();

        if (rows.isEmpty()) {
            throw DomainException.with(new Error("Tipo de veículo não encontrado"));
        }

        return TipoVeiculoOutput.from(TipoVeiculoMapper.toDomain(rows.getFirst()));
    }

    private long totalTiposVeiculo(
            final String marca,
            final String modelo,
            final Integer ano
    ) {
        final var query = """
                select count(tipo)
                from TipoVeiculoJpaEntity tipo
                where (:marca is null or lower(tipo.marca) like :marca)
                  and (:modelo is null or lower(tipo.modelo) like :modelo)
                  and (:ano is null or tipo.ano = :ano)
                """;

        return this.entityManager.createQuery(query, Long.class)
                .setParameter("marca", marca)
                .setParameter("modelo", modelo)
                .setParameter("ano", ano)
                .getSingleResult();
    }
}
