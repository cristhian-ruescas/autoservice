package com.autoservice.infrastructure.tipoveiculo;

import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.tipoveiculo.TipoVeiculoGateway;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.infrastructure.persistence.mapper.TipoVeiculoMapper;
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
        this.repository.save(TipoVeiculoMapper.toEntity(tipoVeiculo));
        return tipoVeiculo;
    }

    @Override
    public Optional<TipoVeiculo> findById(final TipoVeiculoID id) {
        return this.repository.findById(id.getValue()).map(TipoVeiculoMapper::toDomain);
    }

    @Override
    public Optional<TipoVeiculo> findByMarcaModeloAno(final String marca, final String modelo, final Integer ano) {
        return this.repository.findByMarcaModeloAno(marca, modelo, Ano.from(ano))
                .map(TipoVeiculoMapper::toDomain);
    }

    @Override
    public void deleteById(final TipoVeiculoID id) {
        this.repository.deleteById(id.getValue());
    }
}
