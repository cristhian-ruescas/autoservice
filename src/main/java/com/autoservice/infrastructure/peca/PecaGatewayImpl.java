package com.autoservice.infrastructure.peca;

import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.peca.PecaGateway;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.infrastructure.peca.persistence.PecaRepository;
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
        return this.repository.save(peca);
    }

    @Override
    public Peca update(final Peca peca) {
        return this.repository.save(peca);
    }

    @Override
    public Optional<Peca> findById(final PecaID id) {
        return this.repository.findById(id);
    }
}
