package com.autoservice.domain.cliente;

import java.util.Optional;

public interface ClienteGateway {

    Cliente create(Cliente cliente);

    Optional<Cliente> findById(ClienteID id);

    void deleteById(ClienteID id);
}
