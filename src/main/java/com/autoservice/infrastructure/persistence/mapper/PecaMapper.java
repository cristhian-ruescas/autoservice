package com.autoservice.infrastructure.persistence.mapper;

import com.autoservice.domain.estoque.EstoqueID;
import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.infrastructure.persistence.entity.PecaJpaEntity;

public final class PecaMapper {

    private PecaMapper() {
    }

    public static Peca toDomain(final PecaJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Peca.with(
                PecaID.from(entity.getId()),
                entity.getDescricao(),
                entity.getCodigo(),
                entity.getMarca(),
                entity.getValorUnitario(),
                entity.getEstoqueId() == null ? null : EstoqueID.from(entity.getEstoqueId()),
                entity.getTipoVeiculoId() == null ? null : TipoVeiculoID.from(entity.getTipoVeiculoId())
        );
    }

    public static PecaJpaEntity toEntity(final Peca domain) {
        if (domain == null) {
            return null;
        }
        final var entity = new PecaJpaEntity();
        entity.setId(domain.getId().getValue());
        entity.setDescricao(domain.getDescricao());
        entity.setCodigo(domain.getCodigo());
        entity.setMarca(domain.getMarca());
        entity.setValorUnitario(domain.getValorUnitario());
        entity.setEstoqueId(domain.getEstoqueId() == null ? null : domain.getEstoqueId().getValue());
        entity.setTipoVeiculoId(domain.getTipoVeiculoId() == null ? null : domain.getTipoVeiculoId().getValue());
        return entity;
    }
}
