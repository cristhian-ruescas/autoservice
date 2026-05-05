package com.autoservice.infrastructure.itemservico;

import com.autoservice.domain.itemservico.ItemServico;
import com.autoservice.domain.itemservico.ItemServicoGateway;
import com.autoservice.domain.itemservico.ItemServicoID;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.infrastructure.itemservico.persistence.ItemServicoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    public ItemServico update(final ItemServico itemServico) {
        return this.repository.save(itemServico);
    }

    @Override
    public Optional<ItemServico> findById(final ItemServicoID id) {
        return this.repository.findById(id);
    }

    @Override
    public void deleteById(final ItemServicoID id) {
        this.repository.deleteById(id);
    }

    @Override
    public BigDecimal totalByOrdemServicoId(final OrdemServicoID ordemServicoId) {
        return this.repository.totalByOrdemServicoId(ordemServicoId);
    }

    @Override
    public List<ItemServico> findByOrdemServicoId(final OrdemServicoID ordemServicoId) {
        return this.repository.findByOrdemServicoId(ordemServicoId);
    }

    @Override
    public boolean existsByPecaIdAndOrdemServicoStatusNot(final PecaID pecaId, final OrdemServicoStatus status) {
        return this.repository.existsByPecaIdAndOrdemServicoStatusNot(pecaId, status);
    }
}
