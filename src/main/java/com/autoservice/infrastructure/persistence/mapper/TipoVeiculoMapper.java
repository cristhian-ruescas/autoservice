package com.autoservice.infrastructure.persistence.mapper;

import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.infrastructure.persistence.entity.TipoVeiculoJpaEntity;

public final class TipoVeiculoMapper {

    private TipoVeiculoMapper() {
    }

    public static TipoVeiculo toDomain(final TipoVeiculoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return TipoVeiculo.with(entity.getId(), entity.getMarca(), entity.getModelo(), entity.getAno());
    }

    public static TipoVeiculoJpaEntity toEntity(final TipoVeiculo domain) {
        if (domain == null) {
            return null;
        }
        final var entity = new TipoVeiculoJpaEntity();
        entity.setId(domain.getId());
        entity.setMarca(domain.getMarca());
        entity.setModelo(domain.getModelo());
        entity.setAno(domain.getAno());
        return entity;
    }
}
