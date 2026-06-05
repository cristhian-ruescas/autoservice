package com.autoservice.infrastructure.persistence.mapper;

import com.autoservice.domain.ordemcompra.ItemOrdemCompra;
import com.autoservice.infrastructure.persistence.entity.ItemOrdemCompraJpaEntity;

public final class ItemOrdemCompraMapper {

    private ItemOrdemCompraMapper() {
    }

    public static ItemOrdemCompra toDomain(final ItemOrdemCompraJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ItemOrdemCompra.with(
                entity.getId(),
                entity.getOrdemCompraId(),
                entity.getPecaId(),
                entity.getQuantidade()
        );
    }

    public static ItemOrdemCompraJpaEntity toEntity(final ItemOrdemCompra domain) {
        if (domain == null) {
            return null;
        }
        final var entity = new ItemOrdemCompraJpaEntity();
        entity.setId(domain.getId());
        entity.setOrdemCompraId(domain.getOrdemCompraId());
        entity.setPecaId(domain.getPecaId());
        entity.setQuantidade(domain.getQuantidade());
        return entity;
    }
}
