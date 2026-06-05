package com.autoservice.infrastructure.persistence.mapper;

import com.autoservice.domain.ordemcompra.OrdemCompra;
import com.autoservice.infrastructure.persistence.entity.OrdemCompraJpaEntity;

public final class OrdemCompraMapper {

    private OrdemCompraMapper() {
    }

    public static OrdemCompra toDomain(final OrdemCompraJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return OrdemCompra.with(entity.getId(), entity.getStatus(), entity.getDataCompra());
    }

    public static OrdemCompraJpaEntity toEntity(final OrdemCompra domain) {
        if (domain == null) {
            return null;
        }
        final var entity = new OrdemCompraJpaEntity();
        entity.setId(domain.getId());
        entity.setStatus(domain.getStatus());
        entity.setDataCompra(domain.getDataCompra());
        return entity;
    }
}
