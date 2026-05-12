package com.autoservice.infrastructure.usuario;

import com.autoservice.domain.usuario.Usuario;
import com.autoservice.domain.usuario.UsuarioID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<Usuario, UsuarioID> {
    @Query("select u from Usuario u where u.email = :email")
    Optional<Usuario> findByEmail(@Param("email") String email);
}

