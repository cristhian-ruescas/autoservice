package com.autoservice.infrastructure.ordemcompra;

import com.autoservice.domain.ordemcompra.OrdemCompra;
import com.autoservice.domain.ordemcompra.OrdemCompraGateway;
import com.autoservice.domain.ordemcompra.OrdemCompraID;
import com.autoservice.infrastructure.ordemcompra.persistence.OrdemCompraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
public class OrdemCompraGatewayImpl implements OrdemCompraGateway {

    private final OrdemCompraRepository repository;

    public OrdemCompraGatewayImpl(final OrdemCompraRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    @Transactional
    public OrdemCompra create(final OrdemCompra ordemCompra) {
        return this.repository.save(ordemCompra);
    }

    @Override
    @Transactional
    public OrdemCompra update(final OrdemCompra ordemCompra) {
        return this.repository.save(ordemCompra);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdemCompra> findById(final OrdemCompraID id) {
        return this.repository.findById(id);
    }
}
