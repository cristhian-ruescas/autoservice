package com.autoservice.infrastructure.ordemcompra;

import com.autoservice.domain.ordemcompra.ItemOrdemCompra;
import com.autoservice.domain.ordemcompra.ItemOrdemCompraGateway;
import com.autoservice.domain.ordemcompra.OrdemCompraID;
import com.autoservice.infrastructure.ordemcompra.persistence.ItemOrdemCompraRepository;
import com.autoservice.infrastructure.persistence.mapper.ItemOrdemCompraMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ItemOrdemCompraGatewayImpl implements ItemOrdemCompraGateway {

    private final ItemOrdemCompraRepository repository;

    public ItemOrdemCompraGatewayImpl(final ItemOrdemCompraRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    @Transactional
    public ItemOrdemCompra create(final ItemOrdemCompra item) {
        this.repository.save(ItemOrdemCompraMapper.toEntity(item));
        return item;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemOrdemCompra> findByOrdemCompraId(final OrdemCompraID ordemCompraId) {
        return this.repository.findByOrdemCompraId(ordemCompraId.getValue()).stream()
                .map(ItemOrdemCompraMapper::toDomain)
                .toList();
    }
}
