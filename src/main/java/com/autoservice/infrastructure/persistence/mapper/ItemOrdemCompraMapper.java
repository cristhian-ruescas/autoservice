package com.autoservice.infrastructure.persistence.mapper;

import com.autoservice.domain.ordemcompra.ItemOrdemCompra;
import com.autoservice.domain.ordemcompra.ItemOrdemCompraID;
import com.autoservice.domain.ordemcompra.OrdemCompraID;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.infrastructure.persistence.entity.ItemOrdemCompraJpaEntity;

public final class ItemOrdemCompraMapper {

    private ItemOrdemCompraMapper() {
    }

    public static ItemOrdemCompra toDomain(final ItemOrdemCompraJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ItemOrdemCompra.with(
                ItemOrdemCompraID.from(entity.getId()),
                OrdemCompraID.from(entity.getOrdemCompraId()),
                PecaID.from(entity.getPecaId()),
                entity.getQuantidade()
        );
    }

    public static ItemOrdemCompraJpaEntity toEntity(final ItemOrdemCompra domain) {
        if (domain == null) {
            return null;
        }
        final var entity = new ItemOrdemCompraJpaEntity();
        entity.setId(domain.getId().getValue());
        entity.setOrdemCompraId(domain.getOrdemCompraId().getValue());
        entity.setPecaId(domain.getPecaId().getValue());
        entity.setQuantidade(domain.getQuantidade());
        return entity;
    }
}
