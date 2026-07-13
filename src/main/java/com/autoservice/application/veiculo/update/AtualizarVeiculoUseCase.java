package com.autoservice.application.veiculo.update;

import com.autoservice.application.UseCase;
import com.autoservice.application.tipoveiculo.TipoVeiculoResolver;
import com.autoservice.application.veiculo.query.VeiculoOutput;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.domain.veiculo.VeiculoGateway;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.domain.veiculo.valueobject.Cor;
import com.autoservice.domain.veiculo.valueobject.Kilometragem;
import com.autoservice.domain.veiculo.valueobject.Placa;
import com.autoservice.validation.Error;
import com.autoservice.validation.handler.NotificationValidationHandler;

import java.util.Objects;

public class AtualizarVeiculoUseCase extends UseCase<AtualizarVeiculoCommand, VeiculoOutput> {

    private final VeiculoGateway veiculoGateway;
    private final TipoVeiculoResolver tipoVeiculoResolver;

    public AtualizarVeiculoUseCase(
            final VeiculoGateway veiculoGateway,
            final TipoVeiculoResolver tipoVeiculoResolver
    ) {
        this.veiculoGateway = Objects.requireNonNull(veiculoGateway);
        this.tipoVeiculoResolver = Objects.requireNonNull(tipoVeiculoResolver);
    }

    @Override
    public VeiculoOutput execute(final AtualizarVeiculoCommand command) {
        if (command == null) {
            throw DomainException.with(new Error("Comando para atualizar veículo não deve ser nulo"));
        }
        if (command.veiculoId() == null) {
            throw DomainException.with(new Error("Veículo é obrigatório para atualização"));
        }

        final var veiculo = this.veiculoGateway.findById(VeiculoID.from(command.veiculoId()))
                .orElseThrow(() -> DomainException.with(new Error("Veículo não encontrado")));

        final var tipoVeiculo = this.tipoVeiculoResolver.obterOuCriar(
                command.marca(),
                command.modelo(),
                command.ano()
        );
        final var veiculoAtualizado = Veiculo.with(
                veiculo.getId(),
                veiculo.getProprietarioId(),
                tipoVeiculo.getId(),
                Placa.from(command.placa()),
                Cor.from(command.cor()),
                Kilometragem.from(command.kilometragem())
        );
        validate(veiculoAtualizado);

        final var salvo = this.veiculoGateway.update(veiculoAtualizado);

        return new VeiculoOutput(
                salvo.getId().getValue(),
                salvo.getPlaca().getValue(),
                tipoVeiculo.getMarca().getValue(),
                tipoVeiculo.getModelo().getValue(),
                tipoVeiculo.getAno().getValue(),
                salvo.getCor().getValue(),
                salvo.getKilometragem().getValue(),
                null
        );
    }

    private void validate(final Veiculo veiculo) {
        final var handler = new NotificationValidationHandler();
        veiculo.validate(handler);

        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
    }
}
