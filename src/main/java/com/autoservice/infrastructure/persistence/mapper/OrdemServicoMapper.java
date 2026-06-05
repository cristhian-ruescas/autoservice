package com.autoservice.infrastructure.persistence.mapper;

import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.infrastructure.persistence.entity.OrdemServicoJpaEntity;

public final class OrdemServicoMapper {

    private OrdemServicoMapper() {
    }

    public static OrdemServico toDomain(final OrdemServicoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return OrdemServico.with(
                entity.getId(),
                entity.getVeiculoId(),
                entity.getStatus(),
                entity.getDataCriacao(),
                entity.getRelato(),
                entity.getTempoPrevistoExecucaoDias(),
                entity.getTempoPrevistoExecucaoHoras(),
                entity.getIniciadoEm(),
                entity.getFinalizadoEm()
        );
    }

    public static OrdemServicoJpaEntity toEntity(final OrdemServico domain) {
        if (domain == null) {
            return null;
        }
        final var entity = new OrdemServicoJpaEntity();
        entity.setId(domain.getId());
        entity.setVeiculoId(domain.getVeiculoId());
        entity.setStatus(domain.getStatus());
        entity.setDataCriacao(domain.getDataCriacao());
        entity.setRelato(domain.getRelato());
        entity.setTempoPrevistoExecucaoDias(domain.getTempoPrevistoExecucaoDias());
        entity.setTempoPrevistoExecucaoHoras(domain.getTempoPrevistoExecucaoHoras());
        entity.setIniciadoEm(domain.getIniciadoEm());
        entity.setFinalizadoEm(domain.getFinalizadoEm());
        return entity;
    }
}
