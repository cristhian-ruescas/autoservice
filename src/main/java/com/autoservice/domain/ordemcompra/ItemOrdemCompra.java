package com.autoservice.domain.ordemcompra;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;

public class ItemOrdemCompra extends AggregateRoot<ItemOrdemCompraID> {

    private ItemOrdemCompraID id;

    private OrdemCompraID ordemCompraId;

    private PecaID pecaId;

    private Integer quantidade;

    protected ItemOrdemCompra() {
        super();
    }

    private ItemOrdemCompra(
            final ItemOrdemCompraID id,
            final OrdemCompraID ordemCompraId,
            final PecaID pecaId,
            final Integer quantidade
    ) {
        super(id);
        this.id = id;
        this.ordemCompraId = ordemCompraId;
        this.pecaId = pecaId;
        this.quantidade = quantidade;
    }

    public static ItemOrdemCompra newItem(
            final OrdemCompraID ordemCompraId,
            final PecaID pecaId,
            final Integer quantidade
    ) {
        final var item = with(ItemOrdemCompraID.unique(), ordemCompraId, pecaId, quantidade);
        item.validateAndThrow();
        return item;
    }

    public static ItemOrdemCompra with(
            final ItemOrdemCompraID id,
            final OrdemCompraID ordemCompraId,
            final PecaID pecaId,
            final Integer quantidade
    ) {
        return new ItemOrdemCompra(id, ordemCompraId, pecaId, quantidade);
    }

    private void validateAndThrow() {
        final var handler = new NotificationValidationHandler();
        this.validate(handler);
        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
    }

    @Override
    public void validate(final ValidationHandler handler) {
        if (this.ordemCompraId == null) {
            handler.append(new Error("Ordem de compra do item não deve ser nula"));
        }
        if (this.pecaId == null) {
            handler.append(new Error("Peça do item da ordem de compra não deve ser nula"));
        }
        if (this.quantidade == null || this.quantidade <= 0) {
            handler.append(new Error("Quantidade do item da ordem de compra deve ser maior que zero"));
        }
    }

    @Override
    public ItemOrdemCompraID getId() {
        return id;
    }

    public OrdemCompraID getOrdemCompraId() {
        return ordemCompraId;
    }

    public PecaID getPecaId() {
        return pecaId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }
}
