package com.autoservice.domain.estoque;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;

import java.util.List;

public class Estoque extends AggregateRoot<EstoqueID> {

    private EstoqueID id;

    private Integer quantidadeDisponivel;

    private Integer quantidadeMinima;

    private String localizacao;

    protected Estoque() {
        super();
    }

    private Estoque(
            final EstoqueID id,
            final Integer quantidadeDisponivel,
            final Integer quantidadeMinima,
            final String localizacao
    ) {
        super(id);
        this.id = id;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.quantidadeMinima = quantidadeMinima;
        this.localizacao = localizacao;
    }

    public static Estoque newEstoque(
            final Integer quantidadeDisponivel,
            final Integer quantidadeMinima,
            final String localizacao
    ) {
        final Estoque estoque = new Estoque(
                EstoqueID.unique(),
                quantidadeDisponivel,
                quantidadeMinima,
                localizacao
        );
        estoque.validateAndThrow();
        return estoque;
    }

    public static Estoque with(
            final EstoqueID id,
            final Integer quantidadeDisponivel,
            final Integer quantidadeMinima,
            final String localizacao
    ) {
        final Estoque estoque = new Estoque(id, quantidadeDisponivel, quantidadeMinima, localizacao);
        estoque.validateAndThrow();
        return estoque;
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
        if (this.quantidadeDisponivel == null || this.quantidadeDisponivel < 0) {
            handler.append(new Error("Quantidade disponível do estoque não deve ser nula ou negativa"));
        }

        if (this.quantidadeMinima == null || this.quantidadeMinima < 0) {
            handler.append(new Error("Quantidade mínima do estoque não deve ser nula ou negativa"));
        }

        if (this.localizacao != null && this.localizacao.length() > 120) {
            handler.append(new Error("Localização do estoque não deve exceder 120 caracteres"));
        }
    }

    public void baixar(final Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw DomainException.with(List.of(new Error("Quantidade para baixa deve ser maior que zero")));
        }

        if (this.quantidadeDisponivel < quantidade) {
            throw DomainException.with(List.of(new Error("Estoque insuficiente para baixa")));
        }

        this.quantidadeDisponivel -= quantidade;
    }

    public void adicionar(final Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw DomainException.with(List.of(new Error("Quantidade para entrada deve ser maior que zero")));
        }

        this.quantidadeDisponivel += quantidade;
    }

    public void alterarLocalizacao(final String localizacao) {
        this.localizacao = localizacao;
        validateAndThrow();
    }

    @Override
    public EstoqueID getId() {
        return id;
    }

    public Integer getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public Integer getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public String getLocalizacao() {
        return localizacao;
    }
}
