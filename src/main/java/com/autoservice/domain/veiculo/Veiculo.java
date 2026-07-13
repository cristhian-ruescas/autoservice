package com.autoservice.domain.veiculo;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.validators.VeiculoValidator;
import com.autoservice.domain.veiculo.valueobject.Cor;
import com.autoservice.domain.veiculo.valueobject.Kilometragem;
import com.autoservice.domain.veiculo.valueobject.Placa;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;

public class Veiculo extends AggregateRoot<VeiculoID> {

    private VeiculoID id;

    private PessoaID proprietarioId;

    private TipoVeiculoID tipoVeiculoId;

    private Placa placa;
    private Cor cor;
    private Kilometragem kilometragem;

    protected Veiculo() {
        super();
    }

    private Veiculo(
            VeiculoID id,
            PessoaID proprietarioId,
            TipoVeiculoID tipoVeiculoId,
            Placa placa,
            Cor cor,
            Kilometragem kilometragem
    ) {
        super(id);
        this.id = id;
        this.proprietarioId = proprietarioId;
        this.tipoVeiculoId = tipoVeiculoId;
        this.placa = placa;
        this.cor = cor;
        this.kilometragem = kilometragem;
    }

    public static Veiculo newVeiculo(
            PessoaID proprietarioId,
            TipoVeiculoID tipoVeiculoId,
            Placa placa,
            Cor cor,
            Kilometragem kilometragem
    ) {
        Veiculo veiculo = new Veiculo(
                VeiculoID.unique(),
                proprietarioId,
                tipoVeiculoId,
                placa,
                cor,
                kilometragem
        );
        final NotificationValidationHandler handler = new NotificationValidationHandler();
        veiculo.validate(handler);
        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
        return veiculo;
    }

    public static Veiculo with(
            VeiculoID id,
            PessoaID proprietarioId,
            TipoVeiculoID tipoVeiculoId,
            Placa placa,
            Cor cor,
            Kilometragem kilometragem
    ) {
        return new Veiculo(
                id,
                proprietarioId,
                tipoVeiculoId,
                placa,
                cor,
                kilometragem
        );
    }

    @Override
    public void validate(ValidationHandler handler) {
        new VeiculoValidator(this, handler).validate();
    }

    @Override
    public VeiculoID getId() {
        return id;
    }

    public PessoaID getProprietarioId() {
        return proprietarioId;
    }

    public TipoVeiculoID getTipoVeiculoId() {
        return tipoVeiculoId;
    }

    public Placa getPlaca() {
        return placa;
    }

    public Cor getCor() {
        return cor;
    }

    public Kilometragem getKilometragem() {
        return kilometragem;
    }
}
