package com.autoservice.infrastructure.persistence.mapper;

import com.autoservice.domain.usuario.Usuario;
import com.autoservice.domain.usuario.UsuarioID;
import com.autoservice.infrastructure.persistence.entity.UsuarioJpaEntity;

public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static Usuario toDomain(final UsuarioJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Usuario(UsuarioID.from(entity.getId()), entity.getEmail(), entity.getSenha(), entity.getRole());
    }

    public static UsuarioJpaEntity toEntity(final Usuario domain) {
        if (domain == null) {
            return null;
        }
        final var entity = new UsuarioJpaEntity();
        entity.setId(domain.getId().getValue());
        entity.setEmail(domain.getEmail());
        entity.setSenha(domain.getSenha());
        entity.setRole(domain.getRole());
        return entity;
    }
}
