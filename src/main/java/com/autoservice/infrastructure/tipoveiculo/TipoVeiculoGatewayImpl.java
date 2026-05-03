package com.autoservice.infrastructure.tipoveiculo;

import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.tipoveiculo.TipoVeiculoGateway;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.infrastructure.tipoveiculo.persistence.TipoVeiculoRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class TipoVeiculoGatewayImpl implements TipoVeiculoGateway {

    private final TipoVeiculoRepository repository;

    public TipoVeiculoGatewayImpl(final TipoVeiculoRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public TipoVeiculo create(final TipoVeiculo tipoVeiculo) {
        return this.repository.save(tipoVeiculo);
    }

    @Override
    public Optional<TipoVeiculo> findById(final TipoVeiculoID id) {
        return this.repository.findById(id);
    }

    @Override
    public Optional<TipoVeiculo> findByMarcaModeloAno(final String marca, final String modelo, final Integer ano) {
        return this.repository.findByMarcaModeloAno(marca, modelo, ano);
    }
}
