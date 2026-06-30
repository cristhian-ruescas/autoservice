package com.autoservice.infrastructure.persistence.mapper;

import com.autoservice.domain.itemservico.ItemServico;
import com.autoservice.domain.itemservico.ItemServicoID;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.infrastructure.persistence.entity.ItemServicoJpaEntity;

public final class ItemServicoMapper {

    private ItemServicoMapper() {
    }

    public static ItemServico toDomain(final ItemServicoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ItemServico.with(
                ItemServicoID.from(entity.getId()),
                OrdemServicoID.from(entity.getOrdemServicoId()),
                entity.getTipo(),
                entity.getDescricao(),
                entity.getPecaId() == null ? null : PecaID.from(entity.getPecaId()),
                entity.getQuantidade(),
                entity.getValorUnitario()
        );
    }

    public static ItemServicoJpaEntity toEntity(final ItemServico domain) {
        if (domain == null) {
            return null;
        }
        final var entity = new ItemServicoJpaEntity();
        entity.setId(domain.getId().getValue());
        entity.setOrdemServicoId(domain.getOrdemServicoId().getValue());
        entity.setTipo(domain.getTipo());
        entity.setDescricao(domain.getDescricao());
        entity.setPecaId(domain.getPecaId() == null ? null : domain.getPecaId().getValue());
        entity.setQuantidade(domain.getQuantidade());
        entity.setValorUnitario(domain.getValorUnitario());
        return entity;
    }
}
