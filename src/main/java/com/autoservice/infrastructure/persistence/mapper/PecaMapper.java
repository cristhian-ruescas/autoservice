package com.autoservice.infrastructure.persistence.mapper;

import com.autoservice.domain.peca.Peca;
import com.autoservice.infrastructure.persistence.entity.PecaJpaEntity;

public final class PecaMapper {

    private PecaMapper() {
    }

    public static Peca toDomain(final PecaJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Peca.with(
                entity.getId(),
                entity.getDescricao(),
                entity.getCodigo(),
                entity.getMarca(),
                entity.getValorUnitario(),
                entity.getEstoqueId(),
                entity.getTipoVeiculoId()
        );
    }

    public static PecaJpaEntity toEntity(final Peca domain) {
        if (domain == null) {
            return null;
        }
        final var entity = new PecaJpaEntity();
        entity.setId(domain.getId());
        entity.setDescricao(domain.getDescricao());
        entity.setCodigo(domain.getCodigo());
        entity.setMarca(domain.getMarca());
        entity.setValorUnitario(domain.getValorUnitario());
        entity.setEstoqueId(domain.getEstoqueId());
        entity.setTipoVeiculoId(domain.getTipoVeiculoId());
        return entity;
    }
}
