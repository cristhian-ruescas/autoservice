package com.autoservice.infrastructure.persistence.mapper;

import com.autoservice.domain.servico.Servico;
import com.autoservice.infrastructure.persistence.entity.ServicoJpaEntity;

public final class ServicoMapper {

    private ServicoMapper() {
    }

    public static Servico toDomain(final ServicoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Servico.with(entity.getId(), entity.getNome(), entity.getDescricao(), entity.getValorReferencia());
    }

    public static ServicoJpaEntity toEntity(final Servico domain) {
        if (domain == null) {
            return null;
        }
        final var entity = new ServicoJpaEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setDescricao(domain.getDescricao());
        entity.setValorReferencia(domain.getValorReferencia());
        return entity;
    }
}
