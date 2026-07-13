package com.autoservice.infrastructure.persistence.mapper;

import com.autoservice.domain.estoque.Estoque;
import com.autoservice.domain.estoque.EstoqueID;
import com.autoservice.infrastructure.persistence.entity.EstoqueJpaEntity;

public final class EstoqueMapper {

    private EstoqueMapper() {
    }

    public static Estoque toDomain(final EstoqueJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Estoque.with(
                EstoqueID.from(entity.getId()),
                entity.getQuantidadeDisponivel(),
                entity.getQuantidadeMinima(),
                entity.getLocalizacao()
        );
    }

    public static EstoqueJpaEntity toEntity(final Estoque domain) {
        if (domain == null) {
            return null;
        }
        final var entity = new EstoqueJpaEntity();
        entity.setId(domain.getId().getValue());
        entity.setQuantidadeDisponivel(domain.getQuantidadeDisponivel());
        entity.setQuantidadeMinima(domain.getQuantidadeMinima());
        entity.setLocalizacao(domain.getLocalizacao());
        return entity;
    }
}
