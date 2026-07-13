package com.autoservice.domain.itemservico;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.enums.ItemServicoTipo;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;

import java.math.BigDecimal;

public class ItemServico extends AggregateRoot<ItemServicoID> {

    private ItemServicoID id;

    private OrdemServicoID ordemServicoId;

    private ItemServicoTipo tipo;

    private String descricao;

    private PecaID pecaId;

    private Integer quantidade;

    private BigDecimal valorUnitario;

    protected ItemServico() {
        super();
    }

    private ItemServico(
            final ItemServicoID id,
            final OrdemServicoID ordemServicoId,
            final ItemServicoTipo tipo,
            final String descricao,
            final PecaID pecaId,
            final Integer quantidade,
            final BigDecimal valorUnitario
    ) {
        super(id);
        this.id = id;
        this.ordemServicoId = ordemServicoId;
        this.tipo = tipo;
        this.descricao = descricao;
        this.pecaId = pecaId;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    public static ItemServico newServico(
            final OrdemServicoID ordemServicoId,
            final String descricao,
            final BigDecimal valorUnitario
    ) {
        final ItemServico itemServico = new ItemServico(
                ItemServicoID.unique(),
                ordemServicoId,
                ItemServicoTipo.SERVICO,
                descricao,
                null,
                1,
                valorUnitario
        );
        itemServico.validateAndThrow();
        return itemServico;
    }

    public static ItemServico newPeca(
            final OrdemServicoID ordemServicoId,
            final String descricao,
            final PecaID pecaId,
            final Integer quantidade,
            final BigDecimal valorUnitario
    ) {
        final ItemServico itemServico = new ItemServico(
                ItemServicoID.unique(),
                ordemServicoId,
                ItemServicoTipo.PECA,
                descricao,
                pecaId,
                quantidade,
                valorUnitario
        );
        itemServico.validateAndThrow();
        return itemServico;
    }

    public static ItemServico with(
            final ItemServicoID id,
            final OrdemServicoID ordemServicoId,
            final ItemServicoTipo tipo,
            final String descricao,
            final PecaID pecaId,
            final Integer quantidade,
            final BigDecimal valorUnitario
    ) {
        final ItemServico itemServico = new ItemServico(
                id,
                ordemServicoId,
                tipo,
                descricao,
                pecaId,
                quantidade,
                valorUnitario
        );
        itemServico.validateAndThrow();
        return itemServico;
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
        if (this.ordemServicoId == null) {
            handler.append(new Error("Ordem de serviço do item não deve ser nula"));
        }

        if (this.tipo == null) {
            handler.append(new Error("Tipo do item de serviço não deve ser nulo"));
        }

        if (this.descricao == null || this.descricao.isBlank()) {
            handler.append(new Error("Descrição do item de serviço não deve ser nula ou vazia"));
        }

        if (this.quantidade == null || this.quantidade <= 0) {
            handler.append(new Error("Quantidade do item de serviço deve ser maior que zero"));
        }

        if (this.valorUnitario == null || this.valorUnitario.signum() < 0) {
            handler.append(new Error("Valor unitário do item de serviço não deve ser nulo ou negativo"));
        }

        if (this.tipo == ItemServicoTipo.PECA && this.pecaId == null) {
            handler.append(new Error("Peça do item de serviço não deve ser nula para item do tipo PECA"));
        }

        if (this.tipo == ItemServicoTipo.SERVICO && this.pecaId != null) {
            handler.append(new Error("Item do tipo SERVICO não deve possuir peça vinculada"));
        }
    }

    public BigDecimal getValorTotal() {
        return this.valorUnitario.multiply(BigDecimal.valueOf(this.quantidade));
    }

    @Override
    public ItemServicoID getId() {
        return id;
    }

    public OrdemServicoID getOrdemServicoId() {
        return ordemServicoId;
    }

    public ItemServicoTipo getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public PecaID getPecaId() {
        return pecaId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }
}
