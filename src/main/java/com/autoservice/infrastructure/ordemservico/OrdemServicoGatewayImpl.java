package com.autoservice.infrastructure.ordemservico;

import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.infrastructure.ordemservico.persistence.OrdemServicoRepository;
import com.autoservice.infrastructure.persistence.mapper.OrdemServicoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
public class OrdemServicoGatewayImpl implements OrdemServicoGateway {

    private final OrdemServicoRepository repository;

    public OrdemServicoGatewayImpl(final OrdemServicoRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    @Transactional
    public OrdemServico create(final OrdemServico ordemServico) {
        this.repository.save(OrdemServicoMapper.toEntity(ordemServico));
        return ordemServico;
    }

    @Override
    @Transactional
    public OrdemServico update(final OrdemServico ordemServico) {
        this.repository.save(OrdemServicoMapper.toEntity(ordemServico));
        return ordemServico;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdemServico> findById(final OrdemServicoID id) {
        return this.repository.findById(id.getValue()).map(OrdemServicoMapper::toDomain);
    }
}
