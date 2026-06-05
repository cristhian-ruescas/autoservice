package com.autoservice.infrastructure.veiculo;

import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.domain.veiculo.VeiculoGateway;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.infrastructure.persistence.mapper.VeiculoMapper;
import com.autoservice.infrastructure.veiculo.persistence.VeiculoRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class VeiculoGatewayImpl implements VeiculoGateway {

    private final VeiculoRepository repository;

    public VeiculoGatewayImpl(final VeiculoRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Veiculo create(final Veiculo veiculo) {
        this.repository.save(VeiculoMapper.toEntity(veiculo));
        return veiculo;
    }

    @Override
    public Veiculo update(final Veiculo veiculo) {
        this.repository.save(VeiculoMapper.toEntity(veiculo));
        return veiculo;
    }

    @Override
    public Optional<Veiculo> findById(final VeiculoID id) {
        return this.repository.findById(id).map(VeiculoMapper::toDomain);
    }

    @Override
    public void deleteById(final VeiculoID id) {
        this.repository.deleteById(id);
    }
}
