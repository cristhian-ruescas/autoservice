package com.autoservice.infrastructure.persistence.mapper;

import com.autoservice.domain.itemservico.ItemServico;
import com.autoservice.infrastructure.persistence.entity.ItemServicoJpaEntity;

public final class ItemServicoMapper {

    private ItemServicoMapper() {
    }

    public static ItemServico toDomain(final ItemServicoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ItemServico.with(
                entity.getId(),
                entity.getOrdemServicoId(),
                entity.getTipo(),
                entity.getDescricao(),
                entity.getPecaId(),
                entity.getQuantidade(),
                entity.getValorUnitario()
        );
    }

    public static ItemServicoJpaEntity toEntity(final ItemServico domain) {
        if (domain == null) {
            return null;
        }
        final var entity = new ItemServicoJpaEntity();
        entity.setId(domain.getId());
        entity.setOrdemServicoId(domain.getOrdemServicoId());
        entity.setTipo(domain.getTipo());
        entity.setDescricao(domain.getDescricao());
        entity.setPecaId(domain.getPecaId());
        entity.setQuantidade(domain.getQuantidade());
        entity.setValorUnitario(domain.getValorUnitario());
        return entity;
    }
}
