package com.autoservice.infrastructure.peca;

import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.peca.PecaGateway;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.infrastructure.peca.persistence.PecaRepository;
import com.autoservice.infrastructure.persistence.mapper.PecaMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class PecaGatewayImpl implements PecaGateway {

    private final PecaRepository repository;

    public PecaGatewayImpl(final PecaRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Peca create(final Peca peca) {
        this.repository.save(PecaMapper.toEntity(peca));
        return peca;
    }

    @Override
    public Peca update(final Peca peca) {
        this.repository.save(PecaMapper.toEntity(peca));
        return peca;
    }

    @Override
    public Optional<Peca> findById(final PecaID id) {
        return this.repository.findById(id.getValue()).map(PecaMapper::toDomain);
    }

    @Override
    public void deleteById(final PecaID id) {
        this.repository.deleteById(id.getValue());
    }
}
