package com.autoservice.infrastructure.estoque;

import com.autoservice.domain.estoque.Estoque;
import com.autoservice.domain.estoque.EstoqueGateway;
import com.autoservice.domain.estoque.EstoqueID;
import com.autoservice.infrastructure.estoque.persistence.EstoqueRepository;
import com.autoservice.infrastructure.persistence.mapper.EstoqueMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
public class EstoqueGatewayImpl implements EstoqueGateway {

    private final EstoqueRepository repository;

    public EstoqueGatewayImpl(final EstoqueRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    @Transactional
    public Estoque create(final Estoque estoque) {
        this.repository.save(EstoqueMapper.toEntity(estoque));
        return estoque;
    }

    @Override
    @Transactional
    public Estoque update(final Estoque estoque) {
        this.repository.save(EstoqueMapper.toEntity(estoque));
        return estoque;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Estoque> findById(final EstoqueID id) {
        return this.repository.findById(id.getValue()).map(EstoqueMapper::toDomain);
    }
}
