package com.autoservice.infrastructure.cliente.persistence;

import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, ClienteID> {
}
