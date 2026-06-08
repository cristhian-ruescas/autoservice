package com.autoservice.infrastructure.servico.query;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.servico.query.GetServicoByIdQuery;
import com.autoservice.application.servico.query.ListServicosQuery;
import com.autoservice.application.servico.query.ServicoOutput;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.servico.ServicoID;
import com.autoservice.infrastructure.persistence.entity.ServicoJpaEntity;
import com.autoservice.infrastructure.persistence.mapper.ServicoMapper;
import com.autoservice.infrastructure.query.PaginacaoValidator;
import com.autoservice.infrastructure.query.QueryFilterNormalizer;
import com.autoservice.validation.Error;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ServicoQueryService implements ListServicosQuery, GetServicoByIdQuery {

    private final EntityManager entityManager;

    public ServicoQueryService(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationOutput<ServicoOutput> listar(final int page, final int size, final String nome) {
        PaginacaoValidator.validar(page, size);
        final var nomeNormalizado = QueryFilterNormalizer.buscaParcial(nome);

        final var query = """
                select s
                from ServicoJpaEntity s
                where (:nome is null or lower(s.nome) like :nome)
                order by s.nome asc
                """;

        final var items = this.entityManager.createQuery(query, ServicoJpaEntity.class)
                .setParameter("nome", nomeNormalizado)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList()
                .stream()
                .map(entity -> ServicoOutput.from(ServicoMapper.toDomain(entity)))
                .toList();

        return PaginationOutput.from(items, page, size, totalServicos(nomeNormalizado));
    }

    @Override
    @Transactional(readOnly = true)
    public ServicoOutput buscarPorId(final UUID id) {
        if (id == null) {
            throw DomainException.with(new Error("Serviço é obrigatório para consulta"));
        }

        final var query = """
                select s
                from ServicoJpaEntity s
                where s.id = :id
                """;

        final var rows = this.entityManager.createQuery(query, ServicoJpaEntity.class)
                .setParameter("id", ServicoID.from(id).getValue())
                .getResultList();

        if (rows.isEmpty()) {
            throw DomainException.with(new Error("Serviço não encontrado"));
        }

        return ServicoOutput.from(ServicoMapper.toDomain(rows.getFirst()));
    }

    private long totalServicos(final String nome) {
        final var query = """
                select count(s)
                from ServicoJpaEntity s
                where (:nome is null or lower(s.nome) like :nome)
                """;

        return this.entityManager.createQuery(query, Long.class)
                .setParameter("nome", nome)
                .getSingleResult();
    }
}
