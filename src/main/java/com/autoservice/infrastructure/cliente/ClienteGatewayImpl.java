package com.autoservice.infrastructure.cliente;

import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteGateway;
import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.infrastructure.cliente.persistence.ClienteRepository;
import com.autoservice.infrastructure.persistence.mapper.ClienteMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class ClienteGatewayImpl implements ClienteGateway {

    private final ClienteRepository repository;

    public ClienteGatewayImpl(final ClienteRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Cliente create(final Cliente cliente) {
        this.repository.save(ClienteMapper.toEntity(cliente));
        return cliente;
    }

    @Override
    public Optional<Cliente> findById(final ClienteID id) {
        return this.repository.findById(id.getValue()).map(ClienteMapper::toDomain);
    }

    @Override
    public void deleteById(final ClienteID id) {
        this.repository.deleteById(id.getValue());
    }
}
