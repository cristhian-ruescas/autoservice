package com.autoservice.domain.ordemcompra;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemcompra.enums.OrdemCompraStatus;
import com.autoservice.domain.ordemcompra.events.OrdemCompraRealizadaEvent;
import com.autoservice.domain.ordemcompra.validators.OrdemCompraValidator;
import com.autoservice.domain.ordemcompra.valueobject.DataCompra;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;

import java.time.LocalDate;
import java.util.List;

public class OrdemCompra extends AggregateRoot<OrdemCompraID> {

    private OrdemCompraID id;

    private OrdemCompraStatus status;

    private DataCompra dataCompra;

    protected OrdemCompra() {
        super();
    }

    private OrdemCompra(
            final OrdemCompraID id,
            final OrdemCompraStatus status,
            final DataCompra dataCompra
    ) {
        super(id);
        this.id = id;
        this.status = status;
        this.dataCompra = dataCompra;
    }

    public static OrdemCompra newOrdemCompra() {
        final OrdemCompra ordemCompra = new OrdemCompra(
                OrdemCompraID.unique(),
                OrdemCompraStatus.PENDENTE,
                DataCompra.from(LocalDate.now())
        );
        ordemCompra.validateAndThrow();
        return ordemCompra;
    }

    public static OrdemCompra with(
            final OrdemCompraID id,
            final OrdemCompraStatus status,
            final DataCompra dataCompra
    ) {
        final OrdemCompra ordemCompra = new OrdemCompra(id, status, dataCompra);
        ordemCompra.validateAndThrow();
        return ordemCompra;
    }

    public void realizar() {
        if (this.status != OrdemCompraStatus.PENDENTE) {
            throw DomainException.with(List.of(
                    new Error("Ordem de compra precisa estar PENDENTE para ser realizada")
            ));
        }

        this.status = OrdemCompraStatus.REALIZADO;
        this.registerEvent(new OrdemCompraRealizadaEvent(this.getId()));
    }

    private void validateAndThrow() {
        final NotificationValidationHandler handler = new NotificationValidationHandler();
        this.validate(handler);
        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new OrdemCompraValidator(this, handler).validate();
    }

    @Override
    public OrdemCompraID getId() {
        return id;
    }

    public OrdemCompraStatus getStatus() {
        return status;
    }

    public DataCompra getDataCompra() {
        return dataCompra;
    }
}
