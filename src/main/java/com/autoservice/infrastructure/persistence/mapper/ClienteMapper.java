package com.autoservice.infrastructure.persistence.mapper;

import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.infrastructure.persistence.entity.ClienteJpaEntity;

public final class ClienteMapper {

    private ClienteMapper() {
    }

    public static Cliente toDomain(final ClienteJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Cliente.with(
                ClienteID.from(entity.getId()),
                PessoaID.from(entity.getPessoaId()),
                entity.getDataCadastro().getValue()
        );
    }

    public static ClienteJpaEntity toEntity(final Cliente domain) {
        if (domain == null) {
            return null;
        }
        final var entity = new ClienteJpaEntity();
        entity.setId(domain.getId().getValue());
        entity.setPessoaId(domain.getPessoaId().getValue());
        entity.setDataCadastro(domain.getDataCadastro());
        return entity;
    }
}
