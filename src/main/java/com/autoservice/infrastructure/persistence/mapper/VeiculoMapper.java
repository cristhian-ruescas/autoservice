package com.autoservice.infrastructure.persistence.mapper;

import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.infrastructure.persistence.entity.VeiculoJpaEntity;

public final class VeiculoMapper {

    private VeiculoMapper() {
    }

    public static Veiculo toDomain(final VeiculoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Veiculo.with(
                VeiculoID.from(entity.getId()),
                PessoaID.from(entity.getProprietarioId()),
                TipoVeiculoID.from(entity.getTipoVeiculoId()),
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
        entity.setId(domain.getId().getValue());
        entity.setProprietarioId(domain.getProprietarioId().getValue());
        entity.setTipoVeiculoId(domain.getTipoVeiculoId().getValue());
        entity.setPlaca(domain.getPlaca());
        entity.setCor(domain.getCor());
        entity.setKilometragem(domain.getKilometragem());
        return entity;
    }
}
