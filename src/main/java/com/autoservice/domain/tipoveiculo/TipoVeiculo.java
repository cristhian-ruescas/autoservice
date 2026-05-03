package com.autoservice.domain.tipoveiculo;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.domain.veiculo.valueobject.Marca;
import com.autoservice.domain.veiculo.valueobject.Modelo;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipo_veiculo", schema = "cadastro")
public class TipoVeiculo extends AggregateRoot<TipoVeiculoID> {

    @EmbeddedId
    private TipoVeiculoID id;

    @Embedded
    private Marca marca;

    @Embedded
    private Modelo modelo;

    @Embedded
    private Ano ano;

    protected TipoVeiculo() {
        super();
    }

    private TipoVeiculo(final TipoVeiculoID id, final Marca marca, final Modelo modelo, final Ano ano) {
        super(id);
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
    }

    public static TipoVeiculo newTipoVeiculo(final Marca marca, final Modelo modelo, final Ano ano) {
        final var tipoVeiculo = new TipoVeiculo(TipoVeiculoID.unique(), marca, modelo, ano);
        tipoVeiculo.validateAndThrow();
        return tipoVeiculo;
    }

    public static TipoVeiculo with(final TipoVeiculoID id, final Marca marca, final Modelo modelo, final Ano ano) {
        final var tipoVeiculo = new TipoVeiculo(id, marca, modelo, ano);
        tipoVeiculo.validateAndThrow();
        return tipoVeiculo;
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
        if (this.marca == null) {
            handler.append(new Error("Marca do tipo de veículo não deve ser nula"));
        }
        if (this.modelo == null) {
            handler.append(new Error("Modelo do tipo de veículo não deve ser nulo"));
        }
        if (this.ano == null) {
            handler.append(new Error("Ano do tipo de veículo não deve ser nulo"));
        }
    }

    @Override
    public TipoVeiculoID getId() {
        return id;
    }

    public Marca getMarca() {
        return marca;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public Ano getAno() {
        return ano;
    }
}
