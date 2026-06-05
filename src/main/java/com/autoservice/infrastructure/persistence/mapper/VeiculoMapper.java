package com.autoservice.infrastructure.persistence.mapper;

import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.infrastructure.persistence.entity.VeiculoJpaEntity;

public final class VeiculoMapper {

    private VeiculoMapper() {
    }

    public static Veiculo toDomain(final VeiculoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Veiculo.with(
                entity.getId(),
                entity.getProprietarioId(),
                entity.getTipoVeiculoId(),
                entity.getPlaca(),
                entity.getCor(),
                entity.getKilometragem()
        );
    }

    public static VeiculoJpaEntity toEntity(final Veiculo domain) {
        if (domain == null) {
            return null;
        }
        final var entity = new VeiculoJpaEntity();
        entity.setId(domain.getId());
        entity.setProprietarioId(domain.getProprietarioId());
        entity.setTipoVeiculoId(domain.getTipoVeiculoId());
        entity.setPlaca(domain.getPlaca());
        entity.setCor(domain.getCor());
        entity.setKilometragem(domain.getKilometragem());
        return entity;
    }
}
