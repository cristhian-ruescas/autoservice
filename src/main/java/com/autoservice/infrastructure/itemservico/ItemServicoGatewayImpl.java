package com.autoservice.infrastructure.itemservico;

import com.autoservice.domain.itemservico.ItemServico;
import com.autoservice.domain.itemservico.ItemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.infrastructure.itemservico.persistence.ItemServicoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class ItemServicoGatewayImpl implements ItemServicoGateway {

    private final ItemServicoRepository repository;

    public ItemServicoGatewayImpl(final ItemServicoRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public ItemServico create(final ItemServico itemServico) {
        return this.repository.save(itemServico);
    }

    @Override
    public BigDecimal totalByOrdemServicoId(final OrdemServicoID ordemServicoId) {
        return this.repository.totalByOrdemServicoId(ordemServicoId);
    }

    @Override
    public List<ItemServico> findByOrdemServicoId(final OrdemServicoID ordemServicoId) {
        return this.repository.findByOrdemServicoId(ordemServicoId);
    }
}
