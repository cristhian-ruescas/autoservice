package com.autoservice.infrastructure.veiculo;

import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.domain.veiculo.VeiculoGateway;
import com.autoservice.infrastructure.veiculo.persistence.VeiculoRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class VeiculoGatewayImpl implements VeiculoGateway {

    private final VeiculoRepository repository;

    public VeiculoGatewayImpl(final VeiculoRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Veiculo create(final Veiculo veiculo) {
        return this.repository.save(veiculo);
    }
}
