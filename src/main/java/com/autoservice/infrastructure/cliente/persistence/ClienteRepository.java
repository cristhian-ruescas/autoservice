package com.autoservice.infrastructure.cliente.persistence;

import com.autoservice.infrastructure.persistence.entity.ClienteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteJpaEntity, String> {

    Optional<ClienteJpaEntity> findByPessoaId(String pessoaId);
}
