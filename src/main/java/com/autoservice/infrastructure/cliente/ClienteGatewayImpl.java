package com.autoservice.infrastructure.cliente;

import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteGateway;
import com.autoservice.infrastructure.cliente.persistence.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ClienteGatewayImpl implements ClienteGateway {

    private final ClienteRepository repository;

    public ClienteGatewayImpl(final ClienteRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Cliente create(final Cliente cliente) {
        return this.repository.save(cliente);
    }
}
