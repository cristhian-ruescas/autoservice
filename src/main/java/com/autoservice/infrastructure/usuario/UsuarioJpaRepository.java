package com.autoservice.infrastructure.usuario;

import com.autoservice.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, String> {

    @Query("select u from UsuarioJpaEntity u where u.email = :email")
    Optional<UsuarioJpaEntity> findByEmail(@Param("email") String email);
}
