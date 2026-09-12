package com.autoservice.infrastructure.servico;

import com.autoservice.domain.servico.Servico;
import com.autoservice.domain.servico.ServicoGateway;
import com.autoservice.domain.servico.ServicoID;
import com.autoservice.infrastructure.persistence.mapper.ServicoMapper;
import com.autoservice.infrastructure.servico.persistence.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class ServicoGatewayImpl implements ServicoGateway {

    private final ServicoRepository repository;

    public ServicoGatewayImpl(final ServicoRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Servico create(final Servico servico) {
        this.repository.save(ServicoMapper.toEntity(servico));
        return servico;
    }

    @Override
    public Servico update(final Servico servico) {
        this.repository.save(ServicoMapper.toEntity(servico));
        return servico;
    }

    @Override
    public Optional<Servico> findById(final ServicoID id) {
        return this.repository.findById(id.getValue()).map(ServicoMapper::toDomain);
    }

    @Override
    public void deleteById(final ServicoID id) {
        this.repository.deleteById(id.getValue());
    }
}
